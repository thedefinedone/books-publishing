<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Books List</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }
        th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
        th { background-color: #f4f4f4; }
        form { max-width: 400px; padding: 20px; background-color: #f9f9f9; border-radius: 5px; box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1); }
        input, select { margin-bottom: 10px; padding: 8px; width: 100%; border: 1px solid #ccc; border-radius: 3px; }
        button { padding: 10px; background-color: #28a745; color: white; border: none; cursor: pointer; width: 100%; font-weight: bold; }
        button:hover { background-color: #218838; }
        .error { color: red; font-size: 14px; }
    </style>
</head>
<body>
    <h1>📚 Books List</h1>

    <table>
        <tr>
            <th>Title</th>
            <th>Genre</th>
            <th>Publishing Date</th>
        </tr>
        <c:forEach var="book" items="${books}">
            <tr>
                <td>${book.title}</td>
                <td>${book.genre}</td>
                <td>${book.publishingDate != null ? book.publishingDate.date : "No Date Assigned"}</td>
            </tr>
        </c:forEach>
    </table>

    <h2>Add a New Book</h2>
    <form action="${pageContext.request.contextPath}/books/add" method="post">
        <label>Title:</label> 
        <input type="text" name="title" required/>
        <c:if test="${not empty errors.title}">
            <span class="error">${errors.title}</span> <!-- ✅ Error handling -->
        </c:if>

        <label>Genre:</label> 
        <input type="text" name="genre" required/>
        <c:if test="${not empty errors.genre}">
            <span class="error">${errors.genre}</span> <!-- ✅ Error handling -->
        </c:if>

        <label>Publishing Date:</label> 
        <select name="publishingDateId" required> <!-- ✅ Matches controller parameter -->
            <option value="">-- Select Publishing Date --</option>
            <c:forEach var="date" items="${publishingDates}">
                <option value="${date.id}">${date.date}</option>
            </c:forEach>
        </select>

                <c:if test="${not empty _csrf}">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </c:if>

        <button type="submit">Save Book</button>
    </form>
</body>
</html>