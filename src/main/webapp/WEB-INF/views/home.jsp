<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:webpage>
   <article class="file-upload">
		<h1>Compare text files</h1>
		<hr>
		Select (or drag and drop) two plain text or XML Files to compare to each other</br>
		<form action="view" method="POST" enctype="multipart/form-data">
			<input id="file1" name="file1" type="file"><br>
			<input name="file2" type="file"><br>
			<hr>
			Settings file (uses default if empty):</br>
			<input name="settingsFile" type="file" accept=".txt, .conf"><br>
			<hr>
			<input type="submit" value="Compare">
		</form>
	</article>
</t:webpage>
