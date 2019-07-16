CREATE TRIGGER leaveGroupChat
    AFTER DELETE ON Group_Chat_Membership
    FOR EACH ROW EXECUTE PROCEDURE delete_group_chat();


CREATE TRIGGER messageInsert
    AFTER INSERT ON Message
    FOR EACH ROW EXECUTE PROCEDURE create_unread_message();


CREATE TRIGGER removeUser
  BEFORE DELETE ON Users
  FOR EACH ROW EXECUTE PROCEDURE delete_user();