package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        int premiumUser = 0;
        switch(subscription.getSubscriptionType().toString()) {
            case "BASIC":
                premiumUser = 500 + (200 * subscriptionEntryDto.getNoOfScreensRequired());
                break;
            case "PRO":
                premiumUser = 800 + (250 * subscriptionEntryDto.getNoOfScreensRequired());
                break;
            default:
                premiumUser = 1000 + (350 * subscriptionEntryDto.getNoOfScreensRequired());
        }

        subscription.setTotalAmountPaid(premiumUser);
        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);

        return premiumUser;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription userSubscription = user.getSubscription();

        if(userSubscription.getSubscriptionType().toString().equals("ELITE")){
            throw new Exception("Already the best Subscription");
        }

        int previousPrice = user.getSubscription().getTotalAmountPaid();
        int currPrice = 0;

        switch(userSubscription.getSubscriptionType().toString()){
            case "BASIC":
                currPrice = 800 + (250 * user.getSubscription().getNoOfScreensSubscribed());
                userSubscription.setSubscriptionType(SubscriptionType.PRO);
                break;
            default:
                currPrice = 1000 + (350 * user.getSubscription().getNoOfScreensSubscribed());
                userSubscription.setSubscriptionType(SubscriptionType.ELITE);
        }
        subscriptionRepository.save(userSubscription);

        return currPrice-previousPrice;
    }

    public Integer calculateTotalRevenueOfHotstar() throws Exception {

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();

        int revenue = subscriptionList.stream()
                .mapToInt(Subscription::getTotalAmountPaid)
                .sum();

        return revenue;
    }

}
