import { NgModule } from '@angular/core';
import { NavbarComponent } from './navbar/navbar.component';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../material.module';
import { CommonModule } from '@angular/common';
import { FooterComponent } from './footer/footer.component';


@NgModule({
  declarations: [
    NavbarComponent,
    FooterComponent
  ],
  exports: [
    NavbarComponent,
    FooterComponent
  ],
  imports: [
    MaterialModule,
    RouterModule,
    CommonModule,
  ]
})
export class SharedModule { }
