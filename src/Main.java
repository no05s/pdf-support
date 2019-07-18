import com.google.common.base.Stopwatch;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
		LocalDateTime timePoint = LocalDateTime.now();
		LocalDate theDate = timePoint.toLocalDate();
		if (theDate.getYear() > 2019)
			return;

		Stopwatch stopwatch = Stopwatch.createStarted();
		File directory = new File("D:\\pdf\\2차");
		Collection<File> files = FileUtils.listFiles(directory, new String[] { "pdf" }, false);
		Path path = Paths.get("D:\\out\\2차");
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (File file : files) {
			if (file.isFile()) {
				pdf(file, path);
			}
		}
		stopwatch.stop();
		System.out.println(stopwatch);
	}

	private static void pdf(File file, Path path) throws IOException {
		System.out.println(file.getName());
		PDDocument doc = PDDocument.load(file);
		for (PDPage page : doc.getPages()) {
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
			List<PDAnnotation> annotations = page.getAnnotations();

			for (int i = 0; i < annotations.size(); i++) {
				PDAnnotation annot = annotations.get(i);
				if (annot instanceof PDAnnotationLink) {

					PDAnnotationLink link = (PDAnnotationLink) annot;
					PDRectangle rectangle = link.getRectangle();

					PDBorderStyleDictionary underline = new PDBorderStyleDictionary();
					underline.setWidth(0);
					underline.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
					link.setBorderStyle(underline);

					drawLine(contentStream, rectangle);
				} else {
					annotations.remove(annot);
				}
			}
			contentStream.close();
		}

		Path outpath = Paths.get(path.toString(), file.getName());

		doc.save(outpath.toFile());
		doc.close();
	}

	private static void drawLine(PDPageContentStream contentStream, PDRectangle pdRectangle) throws IOException {
		contentStream.setLineWidth(0.6f);
		contentStream.setStrokingColor(Color.blue);
		contentStream.moveTo(pdRectangle.getLowerLeftX(), pdRectangle.getLowerLeftY());
		contentStream.lineTo(pdRectangle.getUpperRightX(), pdRectangle.getLowerLeftY());
		contentStream.stroke();
	}
}
