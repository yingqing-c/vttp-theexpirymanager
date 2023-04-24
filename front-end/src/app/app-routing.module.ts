import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { AddItemComponent } from './pages/add-item/add-item.component';
import { ViewItemsComponent } from './pages/view-items/view-items.component';
import { HomeComponent } from './pages/home/home.component';

const routes: Routes = [
  {
    path: 'home', component: HomeComponent,
  },
  {
    path: 'login', component: LoginComponent,
  },
  {
    path: 'register', component: RegisterComponent,
  },
  {
    path: 'add', component: AddItemComponent,
  },
  {
    path: 'list', component: ViewItemsComponent,
  },
  {
    path: '', redirectTo: 'home', pathMatch: 'full',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
