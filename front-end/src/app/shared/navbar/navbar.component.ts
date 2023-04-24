import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  loggedIn = false;

  constructor(private router: Router, private authService: AuthService) { }

  ngOnInit(): void {
    this.authService.loginStatusObsv.subscribe(
      status => {
        this.loggedIn = status;
        console.log("Logged in status:" + this.loggedIn);
      }
    );
  }

  logout(): void {
    localStorage.clear();
    this.router.navigateByUrl("/home");
    this.loggedIn = false;
  }

}
