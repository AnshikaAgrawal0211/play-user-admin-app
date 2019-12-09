# --- !Ups

create table if not exists "user" (
  "id" SERIAL primary key,
  "firstName" varchar not null,
  "middleName" varchar,
  "lastName" varchar not null,
  "userName" varchar not null,
  "password" varchar not null,
  "mobileNumber" varchar not null,
  "gender" varchar not null,
  "age" int not null
);

# --- !Downs
DROP TABLE "user";
