import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ExpiryService } from '../expiry.service';
import { Item } from '../item.model';

@Component({
  selector: 'app-add-item',
  templateUrl: './add-item.component.html',
  styleUrls: ['./add-item.component.scss']
})
export class AddItemComponent implements OnInit {

  addItemFormGroup!: FormGroup;
  imageBlob!: Blob;
  imageData = "";
  imageUrl: any;

  constructor(private fb: FormBuilder, private expirySvc: ExpiryService) { }

  ngOnInit(): void {
    this.addItemFormGroup = this.fb.group({
      // upload image button
      // product name text field
      // remarks textarea
      // expiry date datepicker
      name: this.fb.control<string>("", Validators.required),
      remarks: this.fb.control<string>(""),
      expiry: this.fb.control<string>("", [Validators.required, this.dateRangeValidator()])
    })
  }

  get name() {
    return this.addItemFormGroup.get("name");
  }

  get remarks() {
    return this.addItemFormGroup.get("remarks");
  }

  get expiry() {
    return this.addItemFormGroup.get("expiry");
  }

  addItem() {
    this.expirySvc.addItem(this.imageBlob,
      this.name?.value,
      this.remarks?.value,
      this.expiry?.value.toDate()).subscribe(res => { });
    this.clearForm(this.addItemFormGroup);
  }

  clearForm(formGroup: FormGroup) {
    // clear form
    formGroup.reset();
    // reset form validations so the inputs are not red
    Object.keys(formGroup.controls).forEach((key) => {
      const control = formGroup.controls[key];
      control.setErrors(null);
    });
  }

  showImage(event: any) {
    if (!event.target.files[0] || event.target.files[0].length == 0) {
      //   console.log("No image loaded");
      return;
    }
    var reader = new FileReader();
    this.imageBlob = event.target.files[0];
    reader.readAsDataURL(this.imageBlob);
    reader.onload = (_event) => {
      this.imageUrl = reader.result;
    }
  }

  /**
   * Makes sure that the date range is after today
   */
  dateRangeValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let valid = false;
      const dueDate = new Date(control.value);
      const today = new Date();
      today.setDate(today.getDate() - 1);
      if (dueDate) {
        valid = dueDate.valueOf() >= today.valueOf();
      }
      if (valid) {
        return null;
      } else {
        return { dateRangeInvalid: true }
      }
    }
  }

  expiryDateFilter = (d: Date | null): boolean => {
    const day = (d || new Date());
    let yst = new Date();
    yst.setDate(yst.getDate() - 1);
    // Prevent days before today from being selected
    return day.valueOf() >= yst.valueOf();
  }
}
