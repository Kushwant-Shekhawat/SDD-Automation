# SDD-Automation Framework - Visual Regression Specification

## 1. OVERVIEW

Visual regression tests capture baseline screenshots and compare subsequent runs
against them. Any pixel difference above a threshold flags the test as failed.
Playwright's built-in `page.screenshot()` is used; diff images are saved to
`build/reports/visual/`.

---

## 2. STRATEGY

| Type | Description |
|------|-------------|
| **Baseline capture** | First run saves PNG baselines to `src/test/resources/visual-baselines/` |
| **Regression run** | Subsequent runs compare against baselines |
| **Diff output** | Diff images saved to `build/reports/visual/diffs/` |
| **Threshold** | Up to **3%** pixel difference is acceptable (anti-aliasing, font rendering, parallel headless variance) |

---

## 3. PAGES TO CAPTURE

| Page | State | Baseline File |
|------|-------|---------------|
| Login page | Default | `login_page.png` |
| Products page | Logged in, default sort | `products_page.png` |
| Product details | Sauce Labs Backpack | `product_details_backpack.png` |
| Cart page | One item in cart | `cart_one_item.png` |
| Checkout step one | Empty form | `checkout_step1.png` |
| Order confirmation | Complete | `checkout_complete.png` |

---

## 4. UTILITY CLASS

File: `src/main/java/org/example/utils/VisualCompareUtil.java`

```java
public class VisualCompareUtil {

    private static final String BASELINE_DIR = "src/test/resources/visual-baselines/";
    private static final String DIFF_DIR     = "build/reports/visual/diffs/";
    private static final double THRESHOLD    = 0.03; // 3%

    public static boolean compareScreenshot(Page page, String baselineName) {
        new File(DIFF_DIR).mkdirs();
        Path actual = Paths.get(DIFF_DIR + "actual_" + baselineName);
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(actual).setFullPage(true));

        Path baseline = Paths.get(BASELINE_DIR + baselineName);
        if (!baseline.toFile().exists()) {
            // First run — save as baseline
            try {
                Files.copy(actual, baseline);
            } catch (IOException e) {
                LoggerUtil.warn("Could not save baseline: " + e.getMessage());
            }
            LoggerUtil.info("Baseline saved: " + baselineName);
            return true;
        }

        return pixelDiffWithinThreshold(baseline, actual, baselineName);
    }

    private static boolean pixelDiffWithinThreshold(Path baseline, Path actual, String name) {
        try {
            BufferedImage imgBaseline = ImageIO.read(baseline.toFile());
            BufferedImage imgActual   = ImageIO.read(actual.toFile());

            if (imgBaseline.getWidth()  != imgActual.getWidth() ||
                imgBaseline.getHeight() != imgActual.getHeight()) {
                LoggerUtil.error("Dimension mismatch for: " + name);
                return false;
            }

            long diffPixels = 0;
            long total = (long) imgBaseline.getWidth() * imgBaseline.getHeight();
            BufferedImage diff = new BufferedImage(
                    imgBaseline.getWidth(), imgBaseline.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < imgBaseline.getHeight(); y++) {
                for (int x = 0; x < imgBaseline.getWidth(); x++) {
                    if (imgBaseline.getRGB(x, y) != imgActual.getRGB(x, y)) {
                        diffPixels++;
                        diff.setRGB(x, y, 0xFF0000); // highlight diff red
                    } else {
                        diff.setRGB(x, y, imgActual.getRGB(x, y));
                    }
                }
            }

            double diffRatio = (double) diffPixels / total;
            Path diffPath = Paths.get(DIFF_DIR + "diff_" + name);
            ImageIO.write(diff, "png", diffPath.toFile());
            LoggerUtil.info(String.format("Visual diff for %s: %.4f%% (%d px)",
                    name, diffRatio * 100, diffPixels));

            return diffRatio <= THRESHOLD;
        } catch (IOException e) {
            LoggerUtil.error("Visual compare failed: " + name, e);
            return false;
        }
    }
}


---

## 5. TEST SCENARIOS

### VR-001: Login page matches baseline
**Category**: Visual  
**Priority**: P2

**Given**: Browser is open  
**When**: Screenshot taken of login page  
**Then**: Pixel diff vs baseline is ≤ 1%  

### VR-002: Products page matches baseline
**Category**: Visual  
**Priority**: P2

**Given**: User is logged in  
**When**: Full-page screenshot taken  
**Then**: Pixel diff vs baseline is ≤ 1%  

### VR-003: Product details page matches baseline
**Category**: Visual  
**Priority**: P2

**Given**: User navigates to Sauce Labs Backpack detail page  
**When**: Screenshot taken  
**Then**: Pixel diff vs baseline is ≤ 1%  

### VR-004: Cart page with one item matches baseline
**Category**: Visual  
**Priority**: P2

**Given**: User has Sauce Labs Backpack in cart  
**When**: Cart page screenshot taken  
**Then**: Pixel diff vs baseline is ≤ 1%  

### VR-005: Checkout complete page matches baseline
**Category**: Visual  
**Priority**: P2

**Given**: User completes checkout  
**When**: Screenshot of confirmation page taken  
**Then**: Pixel diff vs baseline is ≤ 1%  

---

## 6. HOW VISUAL TESTS RUN

Visual regression runs through Cucumber, not a standalone TestNG class. All visual scenarios are in `src/test/resources/features/visual_regression.feature` and tagged `@visual`. They are executed via `VisualSteps.java` which calls `VisualCompareUtil.compareScreenshot()`.

```bash
# Run visual tests (local only)
./gradlew clean test -Dcucumber.filter.tags="@visual"
```

Visual tests are excluded from CI because baselines are Chromium-specific and are not committed for dynamic environments.

---

## 7. RESETTING BASELINES

To re-capture all baselines (e.g. after intentional UI change):

```bash
rm src/test/resources/visual-baselines/*.png
./gradlew clean test -Dcucumber.filter.tags="@visual"
```

The first run auto-saves baselines; subsequent runs compare against them.
