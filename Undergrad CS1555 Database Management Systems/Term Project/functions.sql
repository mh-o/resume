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
    ELSE
        RETURN QUERY SELECT * FROM Contact_List WHERE UserID=NULL AND ContactID=NULL;
    END IF;
END;
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