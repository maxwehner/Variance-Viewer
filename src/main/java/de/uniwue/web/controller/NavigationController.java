package de.uniwue.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import de.uniwue.compare.Annotation;
import de.uniwue.compare.ConnectedContent;
import de.uniwue.compare.Diff;
import de.uniwue.compare.Settings;
import de.uniwue.compare.VarianceType;
import de.uniwue.translate.DiffExporter;
import de.uniwue.translate.TEIToAthenConverter;
import de.uniwue.wa.server.editor.TextAnnotationStruct;
import de.uniwue.web.view.LineCreator;
import difflib.PatchFailedException;

@Controller
public class NavigationController {
	@Autowired
	ServletContext servletContext;

	@RequestMapping(value = "/")
	public String home(Model model) {
		return "home";
	}

	@RequestMapping(value = "/home")
	public String home2(Model model) {
		return "home";
	}

	@RequestMapping(value = "/view", method = RequestMethod.POST)
	public String view(Model model, @RequestParam("file1") MultipartFile file1,
			@RequestParam("file2") MultipartFile file2,
			@RequestParam(value = "settingsFile", required = false) MultipartFile settingsFile) {
		if (!file1.isEmpty() && !file2.isEmpty()) {
			// Read normalize files
			Settings settings = new Settings(StorageManager.getSettings(settingsFile, servletContext));

			// Compare document files
			try {
				String file1Type = file1.getContentType();
				String file2Type = file2.getContentType();

				if (file1Type.equals("text/xml") && file2Type.equals("text/xml")) {
					try {
						TextAnnotationStruct document1 = TEIToAthenConverter.convertTEIToAthen(file1.getInputStream());
						TextAnnotationStruct document2 = TEIToAthenConverter.convertTEIToAthen(file2.getInputStream());
						Collection<Annotation> annotations1 = document1.getAnnotations().stream()
								.map(a -> new Annotation(a)).collect(Collectors.toList());
						Collection<Annotation> annotations2 = document2.getAnnotations().stream()
								.map(a -> new Annotation(a)).collect(Collectors.toList());
						List<ConnectedContent> differences = Diff.compareXML(document1.getText(), document2.getText(),
								annotations1, annotations2, settings);

						model.addAttribute("format", "tei");
						model.addAttribute("exportJSON", DiffExporter.convertToAthenJSONString(document1, differences));
						model.addAttribute("allLines", LineCreator.patch(differences));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					// Interpret as plain text
					String content1 = new String(file1.getBytes(), "UTF-8");
					String content2 = new String(file2.getBytes(), "UTF-8");
					List<ConnectedContent> differences = Diff.comparePlainText(content1, content2, settings);

					model.addAttribute("format", "txt");
					model.addAttribute("exportJSON", DiffExporter.convertToAthenJSONString(content1, differences));
					model.addAttribute("allLines", LineCreator.patch(differences));
				}
				List<VarianceType> variancetypes = new ArrayList<VarianceType>();
				for (VarianceType variancetype : VarianceType.values())
					if (!variancetype.equals(VarianceType.NONE))
						variancetypes.add(variancetype);

				model.addAttribute("variancetypes", variancetypes);
				model.addAttribute("document1name", file1.getOriginalFilename());
				model.addAttribute("document2name", file2.getOriginalFilename());
				model.addAttribute("externalCSS", settings.getExternalCss());
			} catch (IOException e1) {
				return "redirect:/404";
			} catch (PatchFailedException e) {
				return "redirect:/404";
			}

			return "view";
		} else {
			return "redirect:/404";
		}
	}

	@RequestMapping(value = "/404")
	public String error404(Model model) {
		return "404";
	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(10485760);
		return multipartResolver;
	}

}