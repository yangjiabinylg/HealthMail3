
3层架构
com.zjyouth.vo  -view object   value object
bo  - bsiniss object（二期会使用）
com.zjyouth.pojo  --数据库对象


com.zjyouth.common-  用到常量公共类放到这个下面



mybatis 三剑客
1.mybatis generator 根据数据库表自动生成  mybatis的插件
 com.zjyouth.pojo（和数据库一一对应的对象）和dao（接口供service调用）和对应的xml文件（是dao层的实现）
  <plugin>
        <!--  mybatis三剑客中一个   生成generator插件  他会根据我们数据库的数据结构生成dao层文件  我们在改改就好了  -->
        <groupId>org.mybatis.generator</groupId>
        <artifactId>mybatis-generator-maven-plugin</artifactId>
        <version>1.3.2</version>
        <configuration>
          <verbose>true</verbose>
          <overwrite>true</overwrite>
        </configuration>
      </plugin>

generatorConfig.xml  配置插件
