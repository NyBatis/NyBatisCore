# NyBatis Data Mapper Framework
## NyBatisCore

NyBatis는 Java에서 RDB를 다루기 위한 Data Mapper Framework입니다.

MyBatis(IBatis)와 용법 및 철학이 유사합니다만

다음과 같은 부분에서 다소 차이가 있습니다.


1. Mapper 의 XML 설정에 포함된 in / out 설정과 Java 코드 간 dependency 가 없습니다.

   - SQL은 어떤 형식의 파라미터도 입력받을 수 있으며, 출력 파라미터 형식 또한 프로그래머에 의해 자유롭게 결정됩니다.

2. XML Query에서 지원하는 기능이 약간 더 많습니다.

   - if / ifelse / else
   - case / when / default
   - 배열 형식의 파라미터는 자동으로 foreach 형태로 풀어서 binding


3. 간단한 ORM 처리를 지원합니다.

   - Hibernate 처럼 세련되고 막강한 ORM을 지원하진 않습니다만

   - SQL template 엔진을 기반으로, 단순 C/R/U/D 작업에 대한 Object Relation Mapping을 지원합니다.

4. 실무에서 SQL 문장은 부등호를 사용해야 하는 경우가 많습니다.

   이를 위해 XML 에서 '<' 기호를 직접 받아들일 수 있도록 다소 느슨한 규칙을 적용해 xml을 parsing합니다.

