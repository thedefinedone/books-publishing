<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
    <title>Publishing Dates</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }
        th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
        th { background-color: #f4f4f4; }
        form { margin-top: 20px; max-width: 400px; }
        label { display: block; margin-bottom: 5px; }
        input, select { margin-bottom: 10px; padding: 8px; width: 100%; }
        button { padding: 10px; background-color: #007bff; color: white; border: none; cursor: pointer; width: 100%; }
        button:hover { background-color: #0056b3; }
        .cancel-btn { background-color: #dc3545; margin-top: 10px; }
        .cancel-btn:hover { background-color: #c82333; }
        .error { color: red; }
        .message { color: green; }
        a { color: #007bff; text-decoration: none; }
        a:hover { text-decoration: underline; }
        .debug { color: purple; font-size: 0.9em; }
    </style>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            console.log("Page loaded at URL: " + window.location.href);

            var editForm = document.querySelector('form[action$="/publishingDates/update"]');
            var addForm = document.querySelector('form[action$="/publishingDates/add"]');
            if (editForm) {
                console.log("Edit form is visible");
            }
            if (addForm) {
                console.log("Add form is visible");
            }

            var links = document.querySelectorAll('a[href*="/edit/"]');
            links.forEach(function(link) {
                link.addEventListener('click', function(event) {
                    console.log("Edit link clicked: " + link.href);
                    link.style.pointerEvents = 'none';
                    setTimeout(function() {
                        link.style.pointerEvents = 'auto';
                    }, 1000);
                });
            });

            if (editForm) {
                console.log("Edit form detected, preventing auto-submission");
                editForm.addEventListener('submit', function(event) {
                    if (!event.submitter || event.submitter.textContent !== 'Update Date') {
                        console.log("Blocking unintended form submission");
                        event.preventDefault();
                    } else {
                        console.log("Form submitted via 'Update Date' button");
                    }
                });
            }

            if (addForm) {
                console.log("Add form detected, preventing auto-submission");
                addForm.addEventListener('submit', function(event) {
                    if (!event.submitter || event.submitter.textContent !== 'Save Date') {
                        console.log("Blocking unintended form submission");
                        event.preventDefault();
                    } else {
                        console.log("Form submitted via 'Save Date' button");
                    }
                });
            }

            var cancelButtons = document.querySelectorAll('.cancel-btn');
            cancelButtons.forEach(function(button) {
                button.addEventListener('click', function(event) {
                    console.log("Cancel button clicked, navigating to /publishingDates");
                });
            });

            window.addEventListener('beforeunload', function(event) {
                console.log("Page unloading, possible refresh or navigation");
            });

            window.addEventListener('popstate', function(event) {
                console.log("Browser back/forward navigation detected, current URL: " + window.location.href);
            });
        });
    </script>
</head>
<body>
    <h1>Publishing Dates</h1>

    <div class="debug">
        <p>Debug: editingMode = ${editingMode != null ? editingMode : 'not set'}</p>
        <p>Debug: publishingDates size = ${publishingDates.size()}</p>
        <p>Debug: books size = ${books.size()}</p>
        <p>Debug: publishingDate ID = ${publishingDate.id != null ? publishingDate.id : 'not set'}</p>
        <p>Debug: publishingDate Date = ${publishingDate.date != null ? publishingDate.date : 'not set'}</p>
    </div>

    <c:if test="${not empty message}">
        <p class="message">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>

    <h2>Search Publishing Date by Date</h2>
    <form action="${pageContext.request.contextPath}/publishingDates/search" method="get">
        <div>
            <label for="date">Date:</label>
            <input type="date" id="date" name="date" required/>
        </div>
        <button type="submit">Search</button>
    </form>

    <table>
        <tr>
            <th>ID</th>
            <th>Date</th>
            <th>Books</th>
            <th>Actions</th>
        </tr>
        <c:forEach var="publishingDate" items="${publishingDates}">
            <tr>
                <td>${publishingDate.id}</td>
                <td>${publishingDate.date}</td>
                <td>
                    <c:if test="${not empty publishingDate.books}">
                        <c:forEach var="book" items="${publishingDate.books}">
                            ${book.title} (${book.genre})<br/>
                        </c:forEach>
                    </c:if>
                    <c:if test="${empty publishingDate.books}">
                        No books assigned
                    </c:if>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/publishingDates/edit/${publishingDate.id}">Edit</a>
                </td>
            </tr>
        </c:forEach>
    </table>

    <c:if test="${editingMode == false}">
        <h2>Add a Publishing Date</h2>
        <form:form modelAttribute="publishingDate" action="${pageContext.request.contextPath}/publishingDates/add" method="post">
            <div>
                <form:label path="date">Date:</form:label>
                <form:input path="date" type="date"/>
                <form:errors path="date" cssClass="error"/>
            </div>
            <div>
                <label>Books:</label>
                <select name="bookIds" multiple>
                    <c:forEach var="book" items="${books}">
                        <option value="${book.id}">${book.title} (${book.genre})</option>
                    </c:forEach>
                </select>
            </div>
            <button type="submit">Save Date</button>
        </form:form>
    </c:if>

    <c:if test="${editingMode == true}">
        <h2>Edit Publishing Date</h2>
        <form:form modelAttribute="publishingDate" action="${pageContext.request.contextPath}/publishingDates/update" method="post">
            <form:hidden path="id"/>
            <div>
                <form:label path="date">Date:</form:label>
                <form:input path="date" type="date"/>
                <form:errors path="date" cssClass="error"/>
            </div>
            <div>
                <h3>Edit Book Names</h3>
                <select name="bookIds" multiple>
                    <c:forEach var="book" items="${books}">
                        <option value="${book.id}" ${publishingDate.books.contains(book) ? 'selected' : ''}>${book.title} (${book.genre})</option>
                    </c:forEach>
                </select>
            </div>
            <button type="submit">Update Date</button>
            <a href="${pageContext.request.contextPath}/publishingDates" class="cancel-btn" style="display: block; text-align: center;">Cancel</a>
        </form:form>
    </c:if>
</body>
</html>