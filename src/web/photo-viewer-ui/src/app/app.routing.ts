import { Component, ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RemoteComponent } from './components/remote/remote.component';
import { SettingsComponent } from './components/settings/settings.component';
import { ShellComponent } from './components/shell/shell.component';

const appRoutes: Routes = [
    {
        path: "", component: ShellComponent,
        children: [
            { path: "remote", component: RemoteComponent },
            { path: "settings", component: SettingsComponent },
            { path: "", redirectTo: "remote", pathMatch: "full" }
        ]
    }
]

export const Routing: ModuleWithProviders<any> = RouterModule.forRoot(appRoutes);