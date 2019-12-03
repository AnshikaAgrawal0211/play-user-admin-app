# --- !Ups

create table if not exists "user" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "first name" varchar not null,
  "middle name" varchar,
  "last name" varchar not null,
  "username" varchar not null,
  "password" varchar not null,
  "re-enter password" varchar not null,
  "mobile number" varchar not null,
  "gender" varchar not null,
  "age" int not null
);

# --- !Downs
DROP TABLE "user";
