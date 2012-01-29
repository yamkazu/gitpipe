class UrlMappings {

	static mappings = {
        // sing up
        "/signup/created"(controller: "signup", action: "created")
        "/signup"(controller: "signup") {
            action = [GET: "form", POST: "create"]
        }

        // login
        "/login/$action?"(controller: "login")
        "/logout/$action?"(controller: "logout")

        // account
        name updatePassword: "/account/admin/password"(controller: "account") {
            action = "updatePassword"
        }
        "/account/admin"(controller: "account") {
            action = [GET: "showAdmin"]
        }
        "/account"(controller: "account") {
            action = [GET: "showAccount", POST: "updateAccount"]
        }

        // repository
        name repository_tree: "/$username/$project/tree/$ref/$path**?"(controller: "repository", action: "tree")
        name repository_blob: "/$username/$project/blob/$ref/$path**?"(controller: "repository", action: "blob")
        name repository_raw: "/$username/$project/raw/$ref/$path**?"(controller: "repository", action: "raw")
        name repository_commit: "/$username/$project/commit/$id?"(controller: "repository", action: "commit")
        name repository_commits: "/$username/$project/commits/$ref/$path**?"(controller: "repository", action: "commits")

        // project
        name project: "/$username/$project"(controller: "project", action: "show")
        "/project"(controller: "project") {
            action = [GET: "form", POST: "create"]
        }

        // user
        name user: "/$username"(controller: "user", action: "show")

        // other
		name dashboard: "/"(view: "index")
		"500"(view:'/error')
	}
}
