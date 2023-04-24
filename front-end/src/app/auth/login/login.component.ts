import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { LOGGED_IN_KEY, TOKEN_KEY } from 'src/environment';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginFormGroup!: FormGroup;
  loginError = false;
  TITLE = "Login";
  constructor(private router: Router, private fb: FormBuilder,
    private authService: AuthService) { }

  ngOnInit() {
    this.loginFormGroup = this.fb.group({
      username: this.fb.control<string>("", { nonNullable: true, validators: Validators.required }),
      password: this.fb.control<string>("", { nonNullable: true, validators: Validators.required }),
    });
  }

  private get username(): string {
    return this.loginFormGroup.value["username"];
  }

  private get password(): string {
    return this.loginFormGroup.value["password"];
  }

  login() {
    this.authService
      .login(this.username, this.password)
      .subscribe((jwtToken: string) => {
        localStorage.setItem(TOKEN_KEY, jwtToken);
        localStorage.setItem(LOGGED_IN_KEY, "true");
        this.authService.updateLogInStatus(true);
        this.router.navigateByUrl("/add");
      });
    // clear form
    this.loginFormGroup.reset();
    // reset form validations so the inputs are not red
    Object.keys(this.loginFormGroup.controls).forEach((key) => {
      const control = this.loginFormGroup.controls[key];
      control.setErrors(null);
    });
  }



}
