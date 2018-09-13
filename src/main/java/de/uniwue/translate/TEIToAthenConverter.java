package de.uniwue.translate;

import java.io.InputStream;

import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceInitializationException;

import de.uniwue.mk.kall.formatconversion.teireader.reader.TEIReader;
import de.uniwue.wa.server.editor.TextAnnotationStruct;

public class TEIToAthenConverter {
	
	/**
	 * Reads an input stream from an TEI xml file and outputs an Athen annotation struct
	 * 
	 * @param is
	 *            TEI input stream
	 * @return Athen annotation struct
	 *
	 */
	public static TextAnnotationStruct convertTEIToAthen(InputStream is) {
		// Create UIMA CAS from TEI xml file InputStream
		CAS cas;
		try {
			cas = new TEIReader().readDocument(is, false, null).getFirst();
		} catch (ResourceInitializationException e) {
			throw new IllegalArgumentException(
					"Unsupported filetype, can't be converted to Athen. InputStream must be derived from a TEI xml file.");
		}

		// Wrap CAS into webAthen struct
		return new TextAnnotationStruct(cas, null);
	}
}