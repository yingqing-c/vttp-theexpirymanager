import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AUTH_ENDPOINT, LOGGED_IN_KEY, SERVER_ENDPOINT } from 'src/environment';
import { User } from './user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private headers = new HttpHeaders().set('Content-Type', 'application/json; charset=utf-8');
  private logInStatus: BehaviorSubject<boolean>;
  public loginStatusObsv: Observable<boolean>;

  constructor(private http: HttpClient) {
    let loggedIn = localStorage.getItem(LOGGED_IN_KEY);
    if (loggedIn && loggedIn == "true") {
      this.logInStatus = new BehaviorSubject(true);
    } else {
      this.logInStatus = new BehaviorSubject(false);
    }
    this.loginStatusObsv = this.logInStatus.asObservable();
  }


  login(username: string, password: string): Observable<string> {
    return this.http.post<string>(AUTH_ENDPOINT + "login", { "username": username, "password": password }, { headers: this.headers, responseType: "text" as "json" });
  }

  register(email: string, username: string, password: string): Observable<string> {
    return this.http.post<string>(
      AUTH_ENDPOINT + "register", { "username": username, "password": password, "email": email },
      { headers: this.headers, responseType: "text" as "json" }
    )
  };

  updateLogInStatus(status: boolean) {
    this.logInStatus.next(status);
  }
}
