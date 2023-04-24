import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { ExpiryService } from '../expiry.service';
import { Item } from '../item.model';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-view-items',
  templateUrl: './view-items.component.html',
  styleUrls: ['./view-items.component.scss']
})
export class ViewItemsComponent implements OnInit {

  constructor(private expirySvc: ExpiryService, private router: Router, private route: ActivatedRoute) { }

  itemsList: Item[] = [];
  expired: Item[] = [];
  expiring: Item[] = [];
  gCalAccessToken = "";
  showExpired = false;

  ngOnInit(): void {
    this.getListOfItems();
    this.route.queryParams.subscribe(params => {
      if (params["code"]) {
        this.gCalAccessToken = params["code"];
        if (this.expiring.length == 0) {
          // mark a call again in case there are actually expiring items
          this.expirySvc.getItems().subscribe((items: Item[]) => {
            this.expiring = this.expirySvc.getExpiring(items);
            this.expired = this.expirySvc.getExpired(items);
            this.expirySvc.addToGCalendar(this.expiring, this.gCalAccessToken).subscribe();
          })
        } else {
          this.expirySvc.addToGCalendar(this.expiring, this.gCalAccessToken).subscribe();
        }

      }
      this.router.navigate([], { queryParams: {} })
    })
  }

  edit(id: number) {
    console.log(id);
  }

  getListOfItems() {
    return this.expirySvc.getItems().subscribe(
      (items: Item[]) => {
        this.itemsList = items;
        this.expiring = this.expirySvc.getExpiring(items);
        this.expired = this.expirySvc.getExpired(items);
      }
    )
  }

  delete(id: number) {
    this.expirySvc.deleteItem(id).subscribe(
      (response: any) => {
        // refresh the page
        this.ngOnInit()
      });
  }

  addToGCal() {
    if (this.gCalAccessToken == "") this.expirySvc.getGCalAccessToken();
    else {
      this.expirySvc.addToGCalendar(this.expiring, this.gCalAccessToken).subscribe();
    }
  }

  toggleShowExpired() {
    this.showExpired = !this.showExpired;
  }


}
