import { Injectable } from '@angular/core';
import { Item } from './item.model';
import { EXPIRY_ENDPOINT, SERVER_ENDPOINT } from 'src/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class ExpiryService {
  getItems(): Observable<Item[]> {
    return this.http.get<Item[]>(EXPIRY_ENDPOINT + "getItems").pipe(
      map((res: Item[]) => {
        return res;
      })
    )
  }

  getExpiring(items: Item[]): Item[] {
    function notExpired(elem: Item) {
      let expired = false;
      const expiryDate = elem.expiryDate;
      const today = new Date();
      today.setDate(today.getDate() - 1);
      if (expiryDate) {
        expired = expiryDate.valueOf() >= today.valueOf();
      }
      return !expired;
    }
    return items.filter(notExpired);
  }

  getExpired(items: Item[]): Item[] {
    function isExpired(elem: Item) {
      let expired = false;
      const expiryDate = elem.expiryDate;
      const today = new Date();
      today.setDate(today.getDate() - 1);
      if (expiryDate) {
        expired = expiryDate.valueOf() >= today.valueOf();
      }
      return expired;
    }
    return items.filter(isExpired);
  }

  httpOptions = { responseType: "text" as "json" };

  constructor(private http: HttpClient) { }

  addItem(image: Blob, name: string, remarks: string, expiryDate: Date) {
    const formData = new FormData();
    formData.set("image", image);
    formData.set("itemName", name);
    formData.set("remarks", remarks);
    formData.set("expiryStr", expiryDate.toISOString());
    return this.http.post(EXPIRY_ENDPOINT + "addItem", formData, this.httpOptions);
  }

  deleteItem(itemId: number) {
    return this.http.delete(EXPIRY_ENDPOINT + "deleteItem/" + itemId, this.httpOptions);
  }

  getGCalAccessToken() {
    window.open(SERVER_ENDPOINT + "login/oauth2/google", "_self");
  }

  addToGCalendar(items: Item[], accessToken: string) {
    let params = new HttpParams().set('code', accessToken);
    return this.http.post(SERVER_ENDPOINT + "google/addToCalendar", items, { params: params });
  }
}
