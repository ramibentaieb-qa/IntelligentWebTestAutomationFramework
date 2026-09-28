# Intelligent Web Test Automation Framework

This is a small Selenium and Cucumber test framework that I extended so it can explain its own failures. When a test fails, it sends a screenshot, the page HTML and the assertion message to an AI model, and the model says what probably went wrong and what to try next.

I built it to practice UI test automation, and because reading failures is the part of testing I find most tedious.

## Why I did this

A raw stack trace tells you what broke, not why. Take `NoSuchElementException: unable to locate element h6`. It could mean my locator is out of date. It could also mean the login was rejected and the dashboard never loaded. The exception is identical in both cases, and only the screenshot shows the difference.

So I made the framework look at the screenshot too.

## What's in it

- Java 25 and Maven
- Selenium 4.49, running headless Chrome through WebDriverManager
- Cucumber 7 on the JUnit 5 platform
- Page Object pattern (`BasePage`, `LoginPage`, `DashboardPage`)
- GitHub Actions, which runs the tests and uploads the Cucumber HTML report

The tests run against the public OrangeHRM demo site. There is one feature, the login page, with three examples: a valid login, a wrong password and an unknown user. I kept it small on purpose, because the AI part was what I wanted to work on.

## How the failure analysis works

1. When a scenario fails, the `@After` hook takes a screenshot and grabs the page source. The assertion message from the last step is saved too.
2. `AIProviderFactory` reads the `AI_PROVIDER` environment variable and picks a backend.
3. The default backend, `GeminiApiProvider`, sends the screenshot, the first 4,000 characters of the HTML and the assertion message to Google's Gemini API, along with a prompt that asks for a verdict.
4. The model replies with a category, a short summary and a suggested fix.
5. The result is printed in the console and attached to the Cucumber report as `ai-analysis`. The screenshot is attached as `failure-screenshot`.

The categories are:

- `LOCATOR_BROKEN`: a selector no longer matches an element that should be on the page.
- `ASSERTION_MISMATCH`: the app did something different from what the test expected. A wrong password that gets correctly rejected counts here.
- `TIMING_FLAKE`: the element probably would have shown up a moment later.
- `ENVIRONMENT_ISSUE`: infrastructure trouble, like an unreachable server or a browser crash.
- `UNKNOWN`: the evidence isn't enough, or the AI backend wasn't available.

### An early mistake

On one of my first runs I typed a wrong password on purpose. The AI got the reason right but the label wrong:

```
[AI FAILURE ANALYSIS] ENVIRONMENT_ISSUE -- The test failed to locate the dashboard
header 'h6' because the login attempt was rejected with an 'Invalid credentials'
error, leaving the application on the login page. | Suggested fix: Verify and update
the login credentials used in the test...
```

Nothing was wrong with the environment. My prompt listed the categories but never said what separated them, so the model guessed. I rewrote the prompt in `AbstractAIProvider` with a definition for each category and an explicit note that a rejected input is a mismatch, not an environment problem.

## Backends

| `AI_PROVIDER` | Backend                      | Does data leave my machine? |
|---|------------------------------|---|
| `gemini` (default) | Google Gemini API            | Yes |
| `ollama` | Local Ollama, *experimental* | No |


All of them implement one small interface, `AIProvider`, so switching is a single environment variable and no code changes.

### Gemini

1. Get a free API key from [Google AI Studio](https://aistudio.google.com).
2. Set it as an environment variable:
    - Windows PowerShell: `$env:GEMINI_API_KEY="your-key"`
    - macOS or Linux: `export GEMINI_API_KEY=your-key`
    - IntelliJ: Run > Edit Configurations, then add it under Environment variables. IntelliJ does not see variables you set in a separate PowerShell window.
3. For CI, add the same key as a repository secret named `GEMINI_API_KEY` (Settings > Secrets and variables > Actions). The workflow reads it as `${{ secrets.GEMINI_API_KEY }}`.

A few things I learned the hard way:

- The default model is `gemini-3.8-flash`. Google restricts some older models to accounts that already used them, and a fresh key got an HTTP 404 on `gemini-2.5-flash`. If the default ever 404s for you, set `GEMINI_MODEL` to a current model name.
- Gemini sometimes answers with a 503 when it's overloaded. The provider retries up to three times, waiting 2 seconds and then 4, and then gives up. Wrong-key and wrong-model errors are not retried, since retrying can't fix them.
- If there's no API key at all, the tests still run. You just get "AI analysis unavailable" instead of a diagnosis. I didn't want a missing key to break anyone's build.

Never commit your key. It goes in an environment variable or a CI secret, not in the code or the workflow file.

### Ollama (experimental)

`OllamaLocalProvider` is written and follows the same interface, but I haven't got it working. My laptop is an older dual core with 12 GB of RAM and no dedicated GPU, and Ollama kept closing the connection when I tried to run a model. I'm leaving the code in because the design supports it, but treat it as untested. If you have better hardware, `ollama pull gemma3:4b`, then set `AI_PROVIDER=ollama`. I'd like to hear whether it works.

## Running it

In IntelliJ, right-click `TestLoginRunner` and choose Run. From a terminal:

```
mvn clean test
```

The report is written to `target/cucumber-reports/`. Open the HTML file there and expand a failed scenario to see the screenshot and the AI verdict. In CI, download the `cucumber-html-report` artifact from the workflow run.

## Things to know

- The AI can be wrong. I use its verdict as a hint that saves me time, not as a final answer.
- The assertion message is only captured in the last step (`verifyDashboardPage`). If a failure happens earlier, say a missing username field, the AI gets "not captured" as the message and has to work from the screenshot and HTML alone.
- On Gemini's free tier, Google may use prompts to improve its products. Screenshots and page HTML are sent to Google. That's fine for a public demo site, so don't point this at anything with real data.
- Only the login feature is covered.

## Project layout

```
src/main/java/pages/                Page objects
src/main/java/ai/                   AIProvider interface and the Gemini, Ollama and no-op backends
src/test/java/stepdefinitions/      Cucumber steps and the @Before / @After hooks
src/test/java/runners/              JUnit 5 suite runner
src/test/resources/features/        Gherkin feature files
.github/workflows/maven-tests.yml   CI workflow
```