import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'frontend';

  constructor(private httpClient: HttpClient) {
    // this.httpClient.get('localhost:8080/hello').subscribe((data) => {
      this.httpClient.get('hello').subscribe((data) => {
      console.log('-----', data);
    });
    this.httpClient.get('http://localhost:8080/hello').subscribe((data) => {
      console.log('-----', data);
    });
  }
}
