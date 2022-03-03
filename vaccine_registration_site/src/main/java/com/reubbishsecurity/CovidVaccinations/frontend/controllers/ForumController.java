package com.reubbishsecurity.CovidVaccinations.frontend.controllers;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.repository.UserRepository;
import com.reubbishsecurity.CovidVaccinations.forum.entity.Post;
import com.reubbishsecurity.CovidVaccinations.forum.entity.Thread;
import com.reubbishsecurity.CovidVaccinations.forum.repository.PostRepository;
import com.reubbishsecurity.CovidVaccinations.forum.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping(value = "/forum")
public class ForumController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ThreadRepository threadRepository;

    @Autowired
    PostRepository postRepository;

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
        User user = userRepository.findByPps(principal.getName()).get();
        Date date = new Date();
        Thread thread = new Thread(title, date);
        threadRepository.save(thread);

        Post post = new Post(thread, user, date, content);
        postRepository.save(post);
        return "redirect:/forum";
    }

    @PostMapping("/thread/{threadId}/post")
    public String createPost(Principal principal, @PathVariable long threadId, @RequestParam String content) {
        Thread thread = threadRepository.findById(threadId).get();
        User user = userRepository.findByPps(principal.getName()).get();
        Date date = new Date();
        Post post = new Post(thread, user, date, content);
        postRepository.save(post);

        thread.setLatestPost(date);
        thread.incrementReplies();
        threadRepository.save(thread);

        return "redirect:/forum/thread/" + threadId;
    }

}
