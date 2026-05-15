import {Component, Input} from '@angular/core';
import {Button} from "primeng/button";
import {Card} from "primeng/card";
import {Message} from "primeng/message";
import {NgIf} from "@angular/common";
import {PrimeTemplate} from "primeng/api";
import {RouterLink} from "@angular/router";
import {Skeleton} from "primeng/skeleton";
import {TableModule} from "primeng/table";
import {Course} from '../../models/course.model';

@Component({
  selector: 'app-courses-table',
    imports: [
        Button,
        Card,
        Message,
        NgIf,
        PrimeTemplate,
        RouterLink,
        Skeleton,
        TableModule
    ],
  templateUrl: './courses-table.component.html',
  styleUrl: './courses-table.component.scss'
})
export class CoursesTableComponent {
  @Input({required:true}) maxRows!: number;
  @Input({required:true}) courses!: Course[];
  @Input({required:true}) loading!: boolean;
}
