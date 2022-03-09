import { animate, AnimationEvent, state, style, transition, trigger } from '@angular/animations';
import { AfterViewInit, Component, HostListener, OnInit } from '@angular/core';

import { BackgroundService } from './background.service';

@Component({
  selector: 'app-background',
  templateUrl: './background.component.html',
  styleUrls: ['./background.component.css'],
  animations: [
    trigger('move', [
      state(
        'down',
        style({
          transform: 'translateY(-100px)',
        })
      ),
      state(
        'up',
        style({
          transform: 'translateY(100px)',
        })
      ),
      transition('down => up', animate('300ms linear')),
      transition('up => down', animate('300ms linear')),
    ]),
  ],
})
export class BackgroundComponent implements OnInit, AfterViewInit {
  private readonly TILES_ANIMATIONS_STATES = { UP: 'up', DOWN: 'down' };
  state = this.TILES_ANIMATIONS_STATES.UP;
  tiles!: String[][];
  tileHeight!: String;
  gridColumns!: String;

  @HostListener('window:resize', ['$event'])
  private onResize(): void {
    this.setupTiles();
  }

  constructor(private backgroundService: BackgroundService) {
    this.setupTiles();
  }

  ngOnInit(): void {}

  ngAfterViewInit() {
    setTimeout(() => {
      this.state = this.TILES_ANIMATIONS_STATES.DOWN;
    }, 0);
  }
  onEnd(event: AnimationEvent) {
    this.state = this.TILES_ANIMATIONS_STATES.UP;
    if (event.toState === this.TILES_ANIMATIONS_STATES.UP) {
      setTimeout(() => {
        this.state = this.TILES_ANIMATIONS_STATES.DOWN;
      }, 0);
    }
  }

  setupTiles() {
    this.tiles = this.backgroundService.getRandomImages();
    this.tileHeight = this.backgroundService.getTileHeight();
    this.gridColumns = `repeat(${this.tiles.length}, 1fr)`;
  }
}
