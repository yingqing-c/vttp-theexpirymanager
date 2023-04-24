import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AddItemComponent } from './add-item/add-item.component';
import { MaterialModule } from '../material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ViewItemsComponent } from './view-items/view-items.component';
import { HomeComponent } from './home/home.component';


@NgModule({
  declarations: [
    AddItemComponent,
    ViewItemsComponent,
    HomeComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class PagesModule { }
