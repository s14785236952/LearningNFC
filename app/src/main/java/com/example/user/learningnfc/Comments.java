package com.example.user.learningnfc;

public class Comments {

    private String student_name, message, teacher_score;

    public Comments() {
    }

    public Comments(String student_name, String message, String teacher_score) {
        this.student_name = student_name;
        this.message = message;
        this.teacher_score = teacher_score;
    }

    public String getName() {
        return student_name;
    }

    public void setTitle(String name) {
        this.student_name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTeacher_score() {
        return teacher_score;
    }

    public void setTeacher_score(String teacher_score) {
        this.teacher_score = teacher_score;
    }
}
