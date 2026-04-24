package org.example.utils;

import com.microsoft.playwright.Page;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VisualCompareUtil {

    private static final String BASELINE_DIR = "src/test/resources/visual-baselines/";
    private static final String DIFF_DIR = "build/reports/visual/diffs/";
    private static final double THRESHOLD = 0.03; // 3% — accounts for parallel headless rendering variance

    public static boolean compareScreenshot(Page page, String baselineName) {
        new File(DIFF_DIR).mkdirs();
        new File(BASELINE_DIR).mkdirs();

        Path actual = Paths.get(DIFF_DIR + "actual_" + baselineName);
        page.screenshot(new Page.ScreenshotOptions().setPath(actual).setFullPage(true));

        Path baseline = Paths.get(BASELINE_DIR + baselineName);
        if (!baseline.toFile().exists()) {
            try {
                Files.copy(actual, baseline);
                LoggerUtil.info("Visual baseline saved: " + baselineName);
            } catch (IOException e) {
                LoggerUtil.warn("Could not save baseline: " + e.getMessage());
            }
            return true;
        }

        return pixelDiffWithinThreshold(baseline, actual, baselineName);
    }

    private static boolean pixelDiffWithinThreshold(Path baseline, Path actual, String name) {
        try {
            BufferedImage imgBaseline = ImageIO.read(baseline.toFile());
            BufferedImage imgActual = ImageIO.read(actual.toFile());

            if (imgBaseline.getWidth() != imgActual.getWidth()
                    || imgBaseline.getHeight() != imgActual.getHeight()) {
                LoggerUtil.error("Dimension mismatch for: " + name
                        + " baseline=" + imgBaseline.getWidth() + "x" + imgBaseline.getHeight()
                        + " actual=" + imgActual.getWidth() + "x" + imgActual.getHeight());
                return false;
            }

            long diffPixels = 0;
            long total = (long) imgBaseline.getWidth() * imgBaseline.getHeight();
            BufferedImage diff = new BufferedImage(
                    imgBaseline.getWidth(), imgBaseline.getHeight(), BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < imgBaseline.getHeight(); y++) {
                for (int x = 0; x < imgBaseline.getWidth(); x++) {
                    if (imgBaseline.getRGB(x, y) != imgActual.getRGB(x, y)) {
                        diffPixels++;
                        diff.setRGB(x, y, 0xFF0000);
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
            LoggerUtil.error("Visual compare failed for: " + name, e);
            return false;
        }
    }
}
