<dependencyManagement>
  <dependencies>
    <!-- BOM de Quarkus -->
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-bom</artifactId>
      <version>${quarkus.platform.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>

    <!-- Excluir resteasy desde cualquier sitio -->
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy</artifactId>
      <version>${quarkus.platform.version}</version>
      <scope>provided</scope>
      <exclusions>
        <exclusion>
          <groupId>*</groupId>
          <artifactId>*</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
  </dependencies>
</dependencyManagement>
