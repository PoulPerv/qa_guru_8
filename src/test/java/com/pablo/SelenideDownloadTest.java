package com.pablo;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.selenide.Selenide;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;

public class SelenideDownloadTest {

    @Test
    public void downloadTest() throws Exception {
        Selenide.open("https://github.com/Apress/java-unit-testing-with-junit-5/blob/master/LICENSE.txt");
        File textFile = $("#raw-url").download();
        try (InputStream is = new FileInputStream(textFile)) {
            byte[] fileContent = is.readAllBytes();
            String strContent = new String(fileContent, StandardCharsets.UTF_8);
            org.assertj.core.api.Assertions.assertThat(strContent).contains("WARRANTY");
        }
    }

    @Test void pdfParsingTest () throws Exception{
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/index.pdf")){
PDF pdf = new PDF(stream);
            Assertions.assertEquals(93, pdf.numberOfPages);
           assertThat (pdf, new ContainsExactText("123")) ;
        }
    }

    @Test void xlsParsingTest () throws Exception{
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("xls/sample-xlsx-file.xlsx")){
            XLS xls = new XLS(stream);
            String stringCellValue = xls.excel.getSheetAt(0).getRow(3).getCell(1).getStringCellValue();
            org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("Philip");
        }
    }

    @Test void csvParsingTest () throws Exception{
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("csv/teachers.csv");
             CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))){
            List<String[]> content = reader.readAll();
            org.assertj.core.api.Assertions.assertThat(content).contains(
                    new String[]{"Name", "Surname"},
                    new String[]{"Dmitrii", "Tuchs"},
                    new String[]{"Artem", "Eroshenko"}
            );
        }
    }

    @Test void zipParsingTest () throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/zip/sample-zip-file.zip"));
        ZipInputStream is = new ZipInputStream(getClass().getClassLoader().getResourceAsStream ("zip/sample-zip-file.zip"));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null){
            org.assertj.core.api.Assertions.assertThat(entry.getName()).isEqualTo("sample.txt");
            try (InputStream inputStream = zf.getInputStream(entry)){

            }
        }
    }
}
