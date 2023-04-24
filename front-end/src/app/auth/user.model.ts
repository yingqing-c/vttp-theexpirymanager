export class User {

    public username: string;
    public password: string;
    public email: string;
    public roles: string[];

    constructor(user: User = { username: "", password: "", email: "", roles: [] }) {
        this.username = user.username;
        this.password = user.password;
        this.email = user.email;
        this.roles = user.roles;
    }
}
