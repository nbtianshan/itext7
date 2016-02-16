package com.itextpdf.forms;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfFormCopyTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldsCopyTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldsCopyTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 13)
    })
    @Ignore("Merging forms with the same name: widget annotation of old field is removed")
    public void copyFieldsTest01() throws IOException, InterruptedException {
        String srcFilename1 = sourceFolder + "appearances1.pdf";
        String srcFilename2 = sourceFolder + "fieldsOn2-sPage.pdf";
        String srcFilename3 = sourceFolder + "fieldsOn3-sPage.pdf";

        String filename = destinationFolder + "copyFields01.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename1)));
        PdfDocument doc2 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename2)));
        PdfDocument doc3 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename3)));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(filename)));

        doc3.copyPagesTo(1, doc3.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        doc2.copyPagesTo(1, doc2.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        doc1.copyPagesTo(1, doc1.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest02() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "hello_with_comments.pdf";

        String filename = destinationFolder + "copyFields02.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename)));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(filename)));

        doc1.copyPagesTo(1, doc1.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields02.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest03() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "hello2_with_comments.pdf";

        String filename = destinationFolder + "copyFields03.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename)));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(filename)));

        doc1.copyPagesTo(1, doc1.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields03.pdf", destinationFolder, "diff_"));
    }

    @Test(timeout = 60000)
    public void largeFilePerformanceTest() throws IOException, InterruptedException {
        String srcFilename1 = sourceFolder + "frontpage.pdf";
        String srcFilename2 = sourceFolder + "largeFile.pdf";

        String filename = destinationFolder + "copyLargeFile.pdf";

        long timeStart = System.nanoTime();

        PdfDocument doc1 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename1)));
        PdfDocument doc2 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename2)));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(filename)));

        doc1.copyPagesTo(1, doc1.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        doc2.copyPagesTo(1, doc2.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        System.out.println(((System.nanoTime() - timeStart) / 1000 / 1000));

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyLargeFile.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD)
    })
    public void copyFieldsTest04() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "srcFile1.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(new FileInputStream(srcFilename)));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), destDoc, new PdfPageFormCopier());
        srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), destDoc, new PdfPageFormCopier());

        PdfAcroForm form = PdfAcroForm.getAcroForm(destDoc, false);
        Assert.assertEquals(1, form.getFields().size());
        Assert.assertNotNull(form.getField("Name1"));
        Assert.assertNotNull(form.getField("Name1.1"));

        destDoc.close();
    }

    @Test
    public void copyFieldsTest05() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "srcFile1.pdf";
        String destFilename = destinationFolder + "copyFields05.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(new FileInputStream(srcFilename)));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(new FileOutputStream(destFilename)));

        destDoc.addPage(srcDoc.getFirstPage().copyTo(destDoc, new PdfPageFormCopier()));
        destDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields05.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyPagesWithInheritedResources() throws IOException, InterruptedException {
        String sourceFile = sourceFolder +"AnnotationSampleStandard.pdf";
        String destFile =   destinationFolder + "AnnotationSampleStandard_copy.pdf";
        PdfReader reader = new PdfReader(new FileInputStream(sourceFile));
        PdfWriter writer = new PdfWriter(destFile);
        PdfDocument source = new PdfDocument(reader);
        PdfDocument target = new PdfDocument(writer);
        source.copyPagesTo(1, source.getNumberOfPages(), target, new PdfPageFormCopier());
        target.close();
        Assert.assertNull(new CompareTool().compareByContent(destFile, sourceFolder + "cmp_AnnotationSampleStandard_copy.pdf", destinationFolder, "diff_"));
    }
}
