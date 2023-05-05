package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User user1= userRepository.save(user);
        return user1.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        long count = webSeriesList.stream()
                .filter(web -> {
                    if(subscriptionType == SubscriptionType.BASIC){
                        return web.getSubscriptionType() == SubscriptionType.BASIC;
                    }
                    else if(subscriptionType == SubscriptionType.PRO){
                        return web.getSubscriptionType() == SubscriptionType.PRO || web.getSubscriptionType() == SubscriptionType.BASIC;
                    }
                    else{
                        return true;
                    }
                })
                .count();

        return (int) count;

    }


}
