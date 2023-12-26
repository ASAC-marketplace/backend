# ✨MartketPlace - Back-End
```git
⚡ 2023.11 ~ 2024.01
```

&nbsp;&nbsp; <strong>MartketPlace: </strong> IT분야별 트렌드 분석 및 개인화 맞춤 일자리 추천 서비스
<br />

&nbsp;&nbsp;⭐ 나와 기업가치관 추천<br />
&nbsp;&nbsp;⭐ 직업 유형, 스택 종류, 회사명 등 7가지의 카테고리 검색<br />
&nbsp;&nbsp;⭐ 2023년도 월별 기술 및 스택 트랜드 제공<br />
<br />

## Contributor <br/>
&nbsp;&nbsp;<strong>ED</strong> <br />
&nbsp;&nbsp;<strong>Sarah</strong> <br />
&nbsp;&nbsp;<strong>Lucy</strong> <br />

<br />

## Using Stacks <br/>
```git
📌 Springboot, JPA, Gradle, QueryDsl, PostgreSQL, PostMan, AWS, Docker
```
&nbsp;&nbsp;⭐ <strong>시스템 구성도 & ERD</strong> <br />
<div align="center">
      <img src="" width="600" >
      <p>[시스템 구성도]</p>
      <img src="https://github.com/ASAC-marketplace/backend/assets/101400650/61ea8d4f-c5af-4a2c-a4c5-449767424495" width="800" >
      <p>[ERD]</p></br>
</div>

<br />

## 1. Value <br/>
```git
📌 ValueController.js, ValueDto, sort
```
&nbsp;&nbsp;⭐ 나와 기업가치관 추천 작업 <br />
&nbsp;&nbsp;⭐ <strong>sort</strong> 사용하여 사용자의 5가지 가치관(grow, profit, stable, scale, pay)과 유사한 5개의 기업 추천 알고리즘 <br />
&nbsp;&nbsp;⭐ 해당 기업의 관련 공고 제공 <br />
</br>
<div align="center">
      <img src="https://github.com/oyr-driver/react_google/assets/101400650/255df0f8-57e3-46ac-8476-37ec94d2956b" width="600" >
      <p>[기업 추천 알고리즘]</p></br>
      <img src="https://github.com/oyr-driver/react_google/assets/101400650/27cf93c2-0078-46c9-a93a-d7119b145218" width="900" >
      <p>[PostMan이용 기업 5곳 확인]</p>
</div>
  </br></br>
<div align="left">
  &nbsp;&nbsp;⭐ 실제 제공 화면 <br />
</div>
<br />
<div align="center">
      <img src=""  width="800" >
  </div>
<br />

## 2. Search <br/>
```git
📌 SearchController.js, PostDto, Query, DB
```
&nbsp;&nbsp;⭐ 직업 유형, 스택 종류, 회사명 등 7가지의 카테고리 검색 작업<br />
&nbsp;&nbsp;⭐ 검색 시간 <strong>5초 → 1초</strong>: DB에 string 형태인 직업, 스택을 list형태로 변환된 컬럼 추가로 <strong>시간 단축</strong><br />
&nbsp;&nbsp;⭐ Paging 처리<br />
<div align="center">
      <img src="https://github.com/oyr-driver/react_google/assets/101400650/906db1f0-39f1-437b-9b79-c74c70ea23ba" width="500">
      <p>[DB에 JPA이용하여 list형태로 직업, 스택 추가]</p>
      <img src="https://github.com/oyr-driver/react_google/assets/101400650/1d67c893-253e-436a-8e82-007467151be3" width="800">
      <p>[필터링 Query문]</p>
      <img src="https://github.com/oyr-driver/react_google/assets/101400650/677b9f37-cf12-43a4-a9b7-3a9172bbb36e" width="900">
      <p>[PostMan이용 관련 공고 10개 필터링 및 페이징 처리 확인]</p>
</div></br></br>
<div align="left">
  &nbsp;&nbsp;⭐ 실제 제공 화면 <br />
</div>
<br />
<div align="center">
      <img src="https://github.com/ITcareerfit/FRONT/assets/96722691/6232bad4-d4c1-424e-8526-d2d0b7265f76"  width="800" >
      <img src="https://github.com/ITcareerfit/FRONT/assets/96722691/dbd65567-acb1-4e70-8d2d-b96a5b2c855e"  width="800" >
</div>

## 3. Trend <br/>
```git
📌 TrendController.js, PostDto, Query, DB
```
&nbsp;&nbsp;⭐ 2023년도 월별로 공고 3가지 트랜드 제공 작업<br />
&nbsp;&nbsp;⭐ 1. 프로그래밍 언어, 2. 상반기 IT 직무, 3. 직무별 기술스택 트렌드 노출<br />
<div align="center">
      <img src="https://github.com/oyr-driver/react_google/assets/101400650/43e5eaef-a633-479f-86c0-5320b221e062" width="800">
      <p>[트랜드 Query문]</p>
      <img src="https://github.com/oyr-driver/react_google/assets/101400650/81aec5bc-9ca6-410f-b40d-d89121e1de7a" width="900">
      <p>[PostMan이용 3가지 트랜드 확인]</p>
</div>
</br></br>
<div align="left">
  &nbsp;&nbsp;⭐ 실제 제공 화면 <br />
</div>
<br />
<div align="center">
      <img src="https://github.com/ITcareerfit/FRONT/assets/96722691/fa6bc1de-fff2-47e9-b353-5abeb595f3eb"  width="200" >
      <img src="https://github.com/ITcareerfit/FRONT/assets/96722691/a78151db-f349-4302-989f-9685af2fd1a8"  width="300" >
      <img src="https://github.com/ITcareerfit/FRONT/assets/96722691/c9de6bc7-94b2-41c3-b7bd-c0d8cf6d6682"  width="300" >
     
</div>
<br />
