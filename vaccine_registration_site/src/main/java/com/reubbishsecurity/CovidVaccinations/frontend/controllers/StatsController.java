package com.reubbishsecurity.CovidVaccinations.frontend.controllers;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.util.AgeRange;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.util.Nationality;
import com.reubbishsecurity.CovidVaccinations.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class StatsController {

    private static final long msYear = 31556952000L;
    private static final String[] nationalities = Arrays.stream(Nationality.values()).map(Enum::name).toArray(String[]::new);
    private static final String[] genders = Arrays.stream(User.Gender.values()).map(Enum::name).toArray(String[]::new);
    private static final String[] ageRanges = Arrays.stream(AgeRange.values()).map(Enum::name).toArray(String[]::new);
    private static final String[] activities = Arrays.stream(User.LastActivity.values()).map(Enum::name).toArray(String[]::new);

    @Autowired
    UserRepository userRepository;


    @GetMapping("/statistics/activity")
    public String getStatsActivity(Model model, @RequestParam(defaultValue = "MALE") String gender, @RequestParam(defaultValue = "IRISH") String nationality, @RequestParam(defaultValue = "AGE_0_10") String ageRange) {
        Nationality nat;
        User.Gender gen;
        AgeRange range;
        try {
            nat = Nationality.valueOf(nationality.toUpperCase());
        }
        catch (IllegalArgumentException e){
            nat = Nationality.IRISH;
        }

        try {
            gen = User.Gender.valueOf(gender.toUpperCase());
        }
        catch (IllegalArgumentException e){
            gen = User.Gender.MALE;
        }

        try {
            range = AgeRange.valueOf(ageRange.toUpperCase());
        }
        catch (IllegalArgumentException e){
            range = AgeRange.AGE_20_30;
        }

        Map<String, Long> genderStatsMap = this.getGenderStatsByActivity(gen);
        Map<String, Long> ageStatsMap = this.getAgeRangeStatsByActivity(range);
        Map<String, Long> natStatsMap = this.getNationalityStatsByActivity(nat);

        model.addAttribute("gender", gen.getText());
        model.addAttribute("nationality", nat.name());
        model.addAttribute("ageRange", range.getText());

        model.addAttribute("genderMap",  genderStatsMap);
        model.addAttribute("nationalityMap", natStatsMap);
        model.addAttribute("ageMap", ageStatsMap);

        model.addAttribute("nationalities", nationalities);
        model.addAttribute("genders", genders);
        model.addAttribute("ageRanges", ageRanges);

        return "activity-stats.html";
    }

    @GetMapping("/statistics/peractivity")
    public String getStatsActivity(Model model, @RequestParam(defaultValue = "UNVACCINATED") String activity) {
        User.LastActivity lastActivity;
        try {
            lastActivity = User.LastActivity.valueOf(activity.toUpperCase());
        }
        catch (IllegalArgumentException e){
            lastActivity = User.LastActivity.UNVACCINATED;
        }


        Map<String, Long> genderStatsMap = this.getActivityStatsByGender(lastActivity);
        Map<String, Long> ageStatsMap = this.getActivityStatsByAgeRange(lastActivity);
        Map<String, Long> natStatsMap = this.getActivityStatsByNationality(lastActivity);

        model.addAttribute("activity", lastActivity.getText());

        model.addAttribute("genderMap",  genderStatsMap);
        model.addAttribute("nationalityMap", natStatsMap);
        model.addAttribute("ageMap", ageStatsMap);

        model.addAttribute("activities", activities);

        return "peractivity-stats.html";
    }


    public Map<String, Long> getNationalityStatsByActivity(Nationality nationality){
        Map<String, Long> nationalityStats = new HashMap<>();

        for(User.LastActivity activity: User.LastActivity.values()){
            Long value = userRepository.countAllByNationalityAndLastactivity(nationality, activity);
            if(value != 0L) {
                nationalityStats.put(activity.getText(), value);
            }
        }

        return nationalityStats;
    }

    public Map<String, Long> getGenderStatsByActivity(User.Gender gender){
        Map<String, Long> genderStats = new HashMap<>();

        for(User.LastActivity activity: User.LastActivity.values()){
            genderStats.put(activity.getText(), userRepository.countAllByGenderAndLastactivity(gender, activity));
        }

        return genderStats;
    }

    public Map<String, Long> getAgeRangeStatsByActivity(AgeRange ageRange){
        Map<String, Long> ageStats = new HashMap<>();

        for(User.LastActivity activity: User.LastActivity.values()){
            Date upperDate = new Date(System.currentTimeMillis() - (ageRange.lowerAge * msYear));
            Date lowerDate;
            if (ageRange.upperAge < 0){
                lowerDate = new Date(0);
            }
            else{
                lowerDate = new Date(System.currentTimeMillis() - (ageRange.upperAge * msYear));
            }
            ageStats.put(activity.getText(), userRepository.countAllByLastactivityAndDobBetween(activity, lowerDate, upperDate));
        }

        return ageStats;
    }

    public Map<String, Long> getActivityStatsByGender(User.LastActivity activity){
        Map<String, Long> genderStats = new HashMap<>();

        for(User.Gender gender: User.Gender.values()){
            genderStats.put(gender.getText(), userRepository.countAllByGenderAndLastactivity(gender, activity));
        }

        return genderStats;
    }

    public Map<String, Long> getActivityStatsByNationality(User.LastActivity activity){
        Map<String, Long> nationalityStats = new HashMap<>();

        for(Nationality nationality: Nationality.values()){
            Long value = userRepository.countAllByNationalityAndLastactivity(nationality, activity);
            if(value != 0L) {
                nationalityStats.put(nationality.name(), value);
            }
        }

        return nationalityStats;
    }

    public Map<String, Long> getActivityStatsByAgeRange(User.LastActivity activity){
        Map<String, Long> ageStats = new HashMap<>();

        for(AgeRange ageRange: AgeRange.values()){
            Date upperDate = new Date(System.currentTimeMillis() - (ageRange.lowerAge * msYear));
            Date lowerDate;
            if (ageRange.upperAge < 0){
                lowerDate = new Date(0);
            }
            else{
                lowerDate = new Date(System.currentTimeMillis() - (ageRange.upperAge * msYear));
            }
            ageStats.put(ageRange.getText(), userRepository.countAllByLastactivityAndDobBetween(activity, lowerDate, upperDate));
        }

        return ageStats;
    }

}
