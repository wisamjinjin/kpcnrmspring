<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<!DOCTYPE html>
<html lang="ko">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
        content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>KG이니시스 결제샘플</title>
    <!-- 테스트 JS -->
    <script language="javascript" type="text/javascript" src="https://stgstdpay.inicis.com/stdjs/INIStdPay.js"
        charset="UTF-8"></script>
    <!-- 운영 JS
    <script language="javascript" type="text/javascript" src="https://stdpay.inicis.com/stdjs/INIStdPay.js" charset="UTF-8"></script> -->
    <script type="text/javascript">
        function paybtn() {
            INIStdPay.pay('SendPayForm_id');
        }
    </script>
</head>

<body>

    <!-- 본문 -->
    <main>
        <!-- 페이지타이틀 -->
        <section>
            <div>
                <h2>일반 결제</h2>
                <p>내용을 검토한 후 확인 버튼을 누르세요.</p>
            </div>
        </section>
        <!-- //페이지타이틀 -->

        <!-- 카드CONTENTS -->
        <section>
            <div class="common-container">
                <!-- 유의사항 form에서 name을 소문자로 넘겨줌 -->
                <form name="" id="SendPayForm_id" method="post" class="input-box">
                    <div>
                        <input type="hidden" name="version" value="1.0">
                        <input type="hidden" name="gopaymethod" value="Card:Directbank:vbank">
                        <input type="hidden" name="mid" value="${mid}">
                        <input type="hidden" name="oid" value="${oid}">
                        <input type="hidden" name="use_chkfake" value="${use_chkfake}">
                        <input type="hidden" name="signature" value="${signature}">
                        <input type="hidden" name="verification" value="${verification}">
                        <input type="hidden" name="mKey" value="${mKey}">
                        <input type="hidden" name="currency" value="WON">
                        <input type="hidden" name="returnUrl" value="${returnUrl}">
                        <input type="hidden" name="closeUrl" value="${closeUrl}">
                        <input type="hidden" name="acceptmethod" value="HPP(1):below1000:centerCd(Y)">
                        <br/>
                        <label>구매자</label>
                        <input type="text" name="buyername" value="${buyerName}">
                        <br/>
                        <label>내역</label>
                        <input type="text" name="goodname" value="${goodName}">
                        <br/>
                        <label>금액</label>
                        <input type="text" name="price" value="${price}">
                        <br/>
                        <label>결제시각</label>
                        <input type="text" name="timestamp" value="${timestamp}">
                        <br/>
                        <label>전화번호</label>
                        <input type="text" name="buyertel" value="${buyerTel}"> 
                        <br/>
                        <label>이메일</label>
                        <input type="text" name="buyeremail" value="${buyerEmail}">
                    </div>
                </form>

                <button onclick="paybtn()">결제 요청</button>
                <button onclick="location.href='http://localhost:9000/bidseats?telno=${buyerTel}'">취소</button>
            </div>
        </section>

    </main>

</body>

</html>