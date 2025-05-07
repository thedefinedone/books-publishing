<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Books by Publishing Date</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }
        th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
        th { background-color: #f4f4f4; }
        .error { color: red; }
        .message { color: green; }
    </style>
</head>
<body>
    <h1>Books Published on ${selectedDate}</h1>

    <c:if test="${not empty message}">
        <p class="message">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>

    <table>
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Genre</th>
        </tr>
        <c:forEach var="book" items="${books}">
            <tr>
                <td>${book.id}</td>
                <td>${book.title}</td>
                <td>${book.genre}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty books}">
            <tr>
                <td colspan="3">No books found for this date.</td>
            </tr>
        </c:if>
    </table>

    <a href="${pageContext.request.contextPath}/books">Back to Books List</a>
</body>
</html>