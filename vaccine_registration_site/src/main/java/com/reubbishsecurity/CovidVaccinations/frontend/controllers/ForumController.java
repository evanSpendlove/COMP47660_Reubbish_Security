package com.reubbishsecurity.CovidVaccinations.frontend.controllers;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.repository.UserRepository;
import com.reubbishsecurity.CovidVaccinations.forum.entity.Post;
import com.reubbishsecurity.CovidVaccinations.forum.entity.Thread;
import com.reubbishsecurity.CovidVaccinations.forum.repository.PostRepository;
import com.reubbishsecurity.CovidVaccinations.forum.repository.ThreadRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/forum")
public class ForumController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ThreadRepository threadRepository;

    @Autowired
    PostRepository postRepository;

    private static final Logger LOGGER = LogManager.getLogger(ForumController.class);

    private final int MAXIMUM_TITLE_LENGTH = 250;
    private final int MAXIMUM_CONTENT_LENGTH = 5000;

    @GetMapping("")
    public String index(Model model) {
        List<Thread> threads = threadRepository.findAll();
        threads.sort(Thread::compareTo);
        model.addAttribute("threads", threads);
        return "forum.html";
    }

    @GetMapping("/thread/{threadId}")
    public String thread(Model model, @PathVariable long threadId) {
        Thread thread = threadRepository.findById(threadId).get();
        model.addAttribute("thread", thread);

        List<Post> posts = new ArrayList<>(postRepository.findAllByThread(thread).get());
        posts.sort(Post::compareTo);
        model.addAttribute("posts", posts);
        return "thread.html";
    }

    @GetMapping("/create/thread")
    public String newThread() {
        return "create-thread.html";
    }

    @PostMapping("/create/thread")
    public String createThread(Principal principal, @RequestParam String title, @RequestParam String content) {
        if (title.length() > MAXIMUM_TITLE_LENGTH || content.length() > MAXIMUM_CONTENT_LENGTH) {
            return "redirect:/forum/create/thread?error";
        }

        User user = userRepository.findByPps(principal.getName()).get();
        Date date = new Date();
        Thread thread = new Thread(title, date);
        threadRepository.save(thread);

        Post post = new Post(thread, user, date, content);
        postRepository.save(post);

        LOGGER.debug("User " + user.getId() + " created threat with title = " + title + ", content = " + content);

        return "redirect:/forum";
    }

    @PostMapping("/thread/{threadId}/post")
    public String createPost(Principal principal, @PathVariable long threadId, @RequestParam String content) {
        if (content.length() > MAXIMUM_CONTENT_LENGTH) {
            return "redirect:/forum/thread/" + threadId + "?error";
        }
        Thread thread = threadRepository.findById(threadId).get();
        User user = userRepository.findByPps(principal.getName()).get();
        Date date = new Date();
        Post post = new Post(thread, user, date, content);
        postRepository.save(post);

        thread.setLatestPost(date);
        thread.incrementReplies();
        threadRepository.save(thread);

        LOGGER.debug("User " + user.getId() + " created post with content = " + content);

        return "redirect:/forum/thread/" + threadId;
    }

}
