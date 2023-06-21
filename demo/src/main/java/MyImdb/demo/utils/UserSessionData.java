package MyImdb.demo.utils;


import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class UserSessionData {
    private UserData userData = new UserData();

    public UserData getUserData(){
        return userData;
    }

    public void setUserData(UserData userData){
        this.userData = userData;
    }
}
