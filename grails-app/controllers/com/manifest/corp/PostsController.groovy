package com.manifest.corp

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class PostsController {

    PostsService postsService

    static scaffold = Posts

    static allowedMethods = [save: "POST", update: "POST", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond postsService.list(params), model:[postsCount: postsService.count()]
    }

    def show(Long id) {
        respond postsService.get(id)
    }

    def create() {
        respond new Posts(params)
    }

    def save(Posts posts) {
        if (posts == null) {
            notFound()
            return
        }

        try {
            postsService.save(posts)
        } catch (ValidationException e) {
            respond posts.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'posts.label', default: 'Posts'), posts.id])
                redirect posts
            }
            '*' { respond posts, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond postsService.get(id)
    }

    def update(Posts posts) {
        if (posts == null) {
            notFound()
            return
        }

        try {
            postsService.save(posts)
        } catch (ValidationException e) {
            respond posts.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'posts.label', default: 'Posts'), posts.id])
                redirect posts
            }
            '*'{ respond posts, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        postsService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'posts.label', default: 'Posts'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'posts.label', default: 'Posts'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}