-- This phase2 file is currently unused
-- It exists solely for ease of grading

-- sql statements are embedded in QueryExecutor.java
-- triggers moved to triggers.sql
-- functions moved to functions.sql

---------------------
--sql statements
---------------------
--createUser
INSERT INTO Users VALUES (1234567, 'username', 'Matt', 'T', 'Stoss', 'mts74@pitt.edu');

--addToContacts
INSERT INTO Contact_List VALUES (1234567, 7654321);

--displayContacts
SELECT * FROM Users WHERE UserID IN (SELECT ContactID AS UserID FROM Contact_List WHERE UserId=1234567);

--createGroupChat
INSERT INTO Group_Chat VALUES (1111111, 'SampleGroupName');

--addToGroupChat
INSERT INTO Group_Chat_Membership Values (1111111, 1234567);

--leaveGroupChat
DELETE FROM Group_Chat_Membership WHERE GroupID=1111111 AND UserId=1234567;

--sendMessageToUser
INSERT INTO Message (ID, Text, Date, RecipientID, SenderID) VALUES (2222222, 'Hello World', '1984-2-22', 1234567, 7654321);

--sendMessageToGroupChat
INSERT INTO Message (ID, Text, Date, GroupID, SenderID) VALUES (3333333, 'Hello World', '1984-2-22', 1111111, 7654321);

--displayMessages
SELECT * FROM Message M
    WHERE M.RecipientID = 1234567
    OR 1234567 IN (SELECT UserID FROM Group_Chat_Membership G WHERE G.GroupID = M.GroupID);
DELETE FROM Unread_Messages WHERE UserID=1234567;

--displayNewMessages
SELECT * FROM MESSAGE
    WHERE ID IN (SELECT MessageID AS ID FROM Unread_Messages WHERE UserID=1234567);
DELETE FROM Unread_Messages WHERE UserID=1234567;

--threeDegrees
SELECT * FROM three_degrees(91712518, 51172338);

--mostPopular
SELECT ContactID, COUNT(ContactID) FROM Contact_List
    GROUP BY ContactID
    ORDER BY COUNT(ContactID) DESC
    FETCH FIRST 10 ROWS ONLY;

--mostVocal
SELECT SenderID, COUNT(SenderID) FROM Message
    WHERE Date >= now() - interval '11' MONTH
    GROUP BY SenderID
    ORDER BY COUNT(SenderID) DESC
    FETCH FIRST 10 ROWS ONLY;

--dropUser
DELETE FROM Users WHERE UserID=1234567;

---------------------
--triggers
---------------------
CREATE TRIGGER leaveGroupChat
    AFTER DELETE ON Group_Chat_Membership
    FOR EACH ROW EXECUTE PROCEDURE delete_group_chat();


CREATE TRIGGER messageInsert
    AFTER INSERT ON Message
    FOR EACH ROW EXECUTE PROCEDURE create_unread_message();


CREATE TRIGGER removeUser
  BEFORE DELETE ON Users
  FOR EACH ROW EXECUTE PROCEDURE delete_user();

---------------------
--functions
---------------------
CREATE OR REPLACE FUNCTION delete_group_chat() RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM Unread_Messages
        WHERE UserID=OLD.UserID AND MessageID IN (SELECT ID AS MessageID FROM Message WHERE GroupID=OLD.GroupID);

    IF (NOT EXISTS(SELECT * FROM Group_Chat_Membership WHERE GroupID=OLD.GroupID))
    THEN
        DELETE FROM Message WHERE GroupID=OLD.GroupID;
        DELETE FROM Group_Chat WHERE ID=OLD.GroupID;
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION create_unread_message() RETURNS TRIGGER AS $$
DECLARE
    group_cursor CURSOR FOR SELECT * FROM Group_Chat_Membership G WHERE G.GroupID = NEW.GroupID;
    group_record Group_Chat_Membership%ROWTYPE;
BEGIN
    IF (NEW.RecipientID IS NOT NULL) THEN
        INSERT INTO Unread_Messages VALUES (NEW.RecipientID, NEW.ID);
    ELSE
        OPEN group_cursor;
        LOOP
            FETCH group_cursor INTO group_record;
            IF NOT FOUND THEN EXIT;
            END IF;
            INSERT INTO Unread_Messages VALUES (group_record.UserID, NEW.ID);
        END LOOP;
        CLOSE group_cursor;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION three_degrees(A Contact_List.UserID%TYPE, B Contact_List.ContactID%TYPE) RETURNS SETOF Contact_List AS $$
BEGIN
    IF (SELECT COUNT(*) FROM Contact_List WHERE UserId=A AND ContactID=B)
        THEN RETURN QUERY (SELECT * FROM Contact_List WHERE UserId=A AND ContactID=B);
    ELSIF (SELECT COUNT(*) FROM Contact_List WHERE UserId=B AND ContactID=A)
        THEN RETURN QUERY (SELECT * FROM Contact_List WHERE UserId=B AND ContactID=A);
    ELSIF (SELECT COUNT(*) FROM Contact_List C1, Contact_List C2 WHERE C1.ContactID=C2.UserID AND C1.UserID=A AND C2.ContactID=B)
        THEN RETURN QUERY (
            SELECT C1.UserID, C1.ContactID
            FROM Contact_List C1, Contact_List C2
            WHERE C1.ContactID=C2.UserID AND C1.UserID=A AND C2.ContactID=B
            UNION
            SELECT C2.UserID, C2.ContactID
            FROM Contact_List C1, Contact_List C2
            WHERE C1.ContactID=C2.UserID AND C1.UserID=A AND C2.ContactID=B
        );
    ELSIF (SELECT COUNT(*) FROM Contact_List C1, Contact_List C2 WHERE C1.ContactID=C2.UserID AND C1.UserID=B AND C2.ContactID=A)
        THEN RETURN QUERY (
            SELECT C1.UserID, C1.ContactID
            FROM Contact_List C1, Contact_List C2
            WHERE C1.ContactID=C2.UserID AND C1.UserID=B AND C2.ContactID=A
            UNION
            SELECT C2.UserID, C2.ContactID
            FROM Contact_List C1, Contact_List C2
            WHERE C1.ContactID=C2.UserID AND C1.UserID=B AND C2.ContactID=A
        );
    ELSIF (SELECT COUNT(*) FROM Contact_List C1, Contact_list C2, Contact_List C3
           WHERE C1.ContactID=C2.UserID AND C2.ContactID = C3.UserID AND C1.UserID=A AND C3.ContactID=B)
        THEN RETURN QUERY (
            SELECT C1.UserID, C1.ContactID
            FROM Contact_List C1, Contact_list C2, Contact_List C3
            WHERE C1.ContactID=C2.UserID AND C2.ContactID = C3.UserID AND C1.UserID=A AND C3.ContactID=B
            UNION
            SELECT C2.UserID, C2.ContactID
            FROM Contact_List C1, Contact_list C2, Contact_List C3
            WHERE C1.ContactID=C2.UserID AND C2.ContactID = C3.UserID AND C1.UserID=A AND C3.ContactID=B
            UNION
            SELECT C3.UserID, C3.ContactID
            FROM Contact_List C1, Contact_list C2, Contact_List C3
            WHERE C1.ContactID=C2.UserID AND C2.ContactID = C3.UserID AND C1.UserID=A AND C3.ContactID=B
        );
    ELSIF (SELECT COUNT(*) FROM Contact_List C1, Contact_list C2, Contact_List C3
           WHERE C1.ContactID=C2.UserID AND C2.ContactID = C3.UserID AND C1.UserID=B AND C3.ContactID=A)
        THEN RETURN QUERY (
            SELECT C1.UserID, C1.ContactID
            FROM Contact_List C1, Contact_list C2, Contact_List C3
            WHERE C1.ContactID=C2.UserID AND C2.ContactID = C3.UserID AND C1.UserID=B AND C3.ContactID=A
            UNION
            SELECT C2.UserID, C2.ContactID
            FROM Contact_List C1, Contact_list C2, Contact_List C3
            WHERE C1.ContactID=C2.UserID AND C2.ContactID = C3.UserID AND C1.UserID=B AND C3.ContactID=A
            UNION
            SELECT C3.UserID, C3.ContactID
            FROM Contact_List C1, Contact_list C2, Contact_List C3
            WHERE C1.ContactID=C2.UserID AND C2.ContactID = C3.UserID AND C1.UserID=B AND C3.ContactID=A
        );
    ELSE -- Returns empty query
        RETURN QUERY SELECT * FROM Contact_List WHERE UserID=NULL AND ContactID=NULL;
    END IF;
END
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION delete_user() RETURNS TRIGGER AS $$
BEGIN
  UPDATE Message SET SenderID = NULL WHERE SenderID = Old.UserID;
  UPDATE Message SET RecipientID = NULL WHERE RecipientID = Old.UserID;
  DELETE FROM Unread_Messages WHERE UserID = Old.UserID;
  DELETE FROM Message WHERE SenderID=NULL AND RecipientID=NULL;

  DELETE from Contact_List WHERE UserID = Old.UserID OR ContactID = Old.UserID;
  DELETE from Group_Chat_Membership WHERE UserID = Old.UserID;

  RETURN OLD;
END;
$$ LANGUAGE plpgsql;
