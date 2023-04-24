import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['../login/login.component.scss']
})
export class RegisterComponent {
  registerFormGroup!: FormGroup;
  registrationError = false;
  error = "";
  TITLE = "Register";

  constructor(private fb: FormBuilder,
    private authService: AuthService, private router: Router) { }

  ngOnInit() {
    this.registerFormGroup = this.fb.group({
      username: this.fb.control<string>("", { nonNullable: true, validators: [Validators.required, Validators.minLength(3), Validators.maxLength(12)] }),
      password: this.fb.control<string>("", { nonNullable: true, validators: [Validators.required, Validators.minLength(6), Validators.maxLength(20)] }),
      email: this.fb.control<string>("", { nonNullable: true, validators: Validators.email }),
    });
  }

  private get username(): string {
    return this.registerFormGroup.value["username"];
  }

  private get password(): string {
    return this.registerFormGroup.value["password"];
  }

  private get email(): string {
    return this.registerFormGroup.value["email"];
  }

  register() {
    this.authService
      .register(this.email, this.username, this.password)
      .subscribe(
        data => {
          this.router.navigateByUrl("/login");
        },
        error => {
          this.registrationError = true;
          this.error = error.message;
        },
      )
    this.registerFormGroup.reset();
    Object.keys(this.registerFormGroup.controls).forEach((key) => {
      const control = this.registerFormGroup.controls[key];
      control.setErrors(null);
    });
  }
}
