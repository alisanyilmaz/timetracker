package com.ayilmaz.timetracker.timetrackerbff.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebController {

    @GetMapping
    public String getHome() {
        return "forward:/index.html";
    }
}
