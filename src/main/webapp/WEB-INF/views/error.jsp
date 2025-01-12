<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <br/>
        <br/>
        <br/>
        <title>결제 요청 중에 오류가 발생하였습니다.</title>
        
        <script type="text/javascript">
            function redirectToLogin() {
                window.location.href = "http://localhost:9000/userlogin";
            }
        </script>
    </head>
    <body>
        <div> 오류 내용을 참고하세요.</div>
        <div class="error-message">
            <div>"${errorMessage}" Code : "${resultCode}"</div>
        </div>

        <!-- 리다이렉트 버튼 -->
        <button class="redirect-button" onclick="redirectToLogin()">로그인 페이지로 이동</button>
    </body>
</html>
