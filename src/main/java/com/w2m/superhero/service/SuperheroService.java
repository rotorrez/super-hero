<dependency>
  <groupId>com.santander.sgt.apm1953</groupId>
  <artifactId>sgt-apm1953-ppbm</artifactId>
  <version>1.0.1</version>
  <exclusions>
    <exclusion>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy</artifactId>
    </exclusion>
    <exclusion>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-jackson</artifactId>
    </exclusion>
    <!-- Agrega esto también -->
    <exclusion>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-server-common</artifactId>
    </exclusion>
  </exclusions>
</dependency>


  <dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-rest</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-rest-jackson</artifactId>
</dependency>
