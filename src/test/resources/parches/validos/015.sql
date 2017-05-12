

CREATE TABLE parches_sql
(
    nombre character varying(10),
    CONSTRAINT pk_parches_sql PRIMARY KEY(nombre)
);

CREATE TABLE perfil
(
  id_perfil integer NOT NULL,
  nombre character varying(250),
  CONSTRAINT pk_perfil PRIMARY KEY (id_perfil)
);

CREATE TABLE usuario
(
  rut integer NOT NULL,
  nombres character varying(250),
  apellidos character varying(250),
  password character varying(1000),
  email character varying(250),
  fecha_nacimiento date,
  perfil_id integer,
  habilitado boolean,
  CONSTRAINT pk_usuario PRIMARY KEY (rut),
  CONSTRAINT fk_usuario FOREIGN KEY (perfil_id)
      REFERENCES perfil (id_perfil)
);

INSERT INTO parches_sql (nombre) VALUES ('015.sql');

