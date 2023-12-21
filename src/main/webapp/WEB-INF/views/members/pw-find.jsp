<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>비밀번호 찾기 페이지</title>
</head>
<body>

<form class="content" action="pw_auth" method="post">
    <div class="textbox">
        <input id="text" name="name" required="" type="text"/>
        <label for="text">이름</label>
        <div class="error">이름을 입력하세요</div>
    </div>
    <div class="textbox">
        <input id="email" name="email" required="required" type="email"/>
        <label for="email">이메일</label>
        <div class="error">유효하지 않은 이메일주소입니다</div>
    </div>
    <br><br>
    <input type="submit" id="check" value="비밀번호찾기">
</form>

</body>
</html>
