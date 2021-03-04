import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from "@angular/forms"
import { HttpClientModule, HTTP_INTERCEPTORS, HttpClientXsrfModule, HttpClient } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { Routing } from '@app/app.routing';

import { MaterialModule } from '@app/modules/material//material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ShellComponent } from './components/shell/shell.component';
import { RemoteComponent } from './components/remote/remote.component';
import { SettingsComponent } from './components/settings/settings.component';

@NgModule({
  declarations: [
    AppComponent,
    ShellComponent,
    RemoteComponent,
    SettingsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    Routing,
    FormsModule,
    MaterialModule,
    HttpClientModule,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
