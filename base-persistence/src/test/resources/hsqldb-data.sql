SET AUTOCOMMIT FALSE;

INSERT INTO PERSON VALUES(NEXT VALUE FOR PERSON_SEQ, 'Freese', 'Thomas');
INSERT INTO PERSON VALUES(NEXT VALUE FOR PERSON_SEQ, 'Nachname1', 'Vorname1');
INSERT INTO PERSON VALUES(NEXT VALUE FOR PERSON_SEQ, 'Nachname2', 'Vorname2');

--insert into USERS (ID, USERNAME) VALUES(NEXT/CURRENT VALUE FOR USER_SEQ, 'USER 1');

COMMIT;