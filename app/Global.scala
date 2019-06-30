import play.api.{Application, GlobalSettings}
import play.api.db.DB
import anorm._

object Global extends GlobalSettings {

  override def onStart(app: Application)= {

    import play.api.Play.current

    DB.withConnection { implicit connection =>

      SQL("drop table if exists user;").execute()
      SQL("create table if not exists user (  email varchar(255),name varchar(255),password varchar(255) not null);").execute()
      SQL("create table if not exists contactme ( name  varchar(255) not null primary key,email  varchar(255) not null,phone  varchar(255) not null,jobspec varchar(max) not null);").execute()
      //SQL("set ignorecase true;create table if not exists company (id bigint not null,name varchar(255) not null,constraint pk_company primary key (id));create table if not exists computer (id bigint not null,name varchar(255) not null,introduced  timestamp,discontinued timestamp,company_id bigint,constraint pk_computer primary key (id));create sequence company_seq start with 1000;create sequence computer_seq start with 1000;alter table computer add constraint fk_computer_company_1 foreign key (company_id) references company (id) on delete restrict on update restrict;create index ix_computer_company_1 on computer (company_id);").execute()
      SQL("INSERT INTO user (email,name,password) VALUES('kali.tummala@gmail.com','kali.tummala@gmail.com','Sairam786516')").execute()
      //SQL("INSERT INTO books(title,price,author) VALUES('Apache Kafka CookBook','27$','Shapria')").execute()
      //SQL("INSERT INTO books(title,price,author) VALUES('Apache Scala CookBook','10$','Alvin Alexander')").execute()
    }


  }
}
