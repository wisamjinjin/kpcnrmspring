import{l as y,q as d,s as _,N as f,v as m,a as g,c as v,ae as i,F as B,aZ as S,b as A,a$ as C,Q as x}from"./index.c44c2320.js";import{_ as h}from"./plugin-vue_export-helper.21dcd24c.js";const q=["top","middle","bottom"];var F=y({name:"QBadge",props:{color:String,textColor:String,floating:Boolean,transparent:Boolean,multiLine:Boolean,outline:Boolean,rounded:Boolean,label:[Number,String],align:{type:String,validator:e=>q.includes(e)}},setup(e,{slots:l}){const r=d(()=>e.align!==void 0?{verticalAlign:e.align}:null),s=d(()=>{const o=e.outline===!0&&e.color||e.textColor;return`q-badge flex inline items-center no-wrap q-badge--${e.multiLine===!0?"multi":"single"}-line`+(e.outline===!0?" q-badge--outline":e.color!==void 0?` bg-${e.color}`:"")+(o!==void 0?` text-${o}`:"")+(e.floating===!0?" q-badge--floating":"")+(e.rounded===!0?" q-badge--rounded":"")+(e.transparent===!0?" q-badge--transparent":"")});return()=>_("div",{class:s.value,style:r.value,role:"status","aria-label":e.label},f(l.default,e.label!==void 0?[e.label]:[]))}}),I=y({name:"QTr",props:{props:Object,noHover:Boolean},setup(e,{slots:l}){const r=d(()=>"q-tr"+(e.props===void 0||e.props.header===!0?"":" "+e.props.__trClass)+(e.noHover===!0?" q-tr--no-hover":""));return()=>_("tr",{class:r.value},m(l.default))}});const k={class:"seat-grid"},$={__name:"SeatMap",props:{selectedSeats:Array,bidsArray:Array,disabled:Boolean,onSeatClick:{type:Function,required:!0}},emits:["seat-click"],setup(e,{emit:l}){const r=e,s=l,o=n=>{s("seat-click",n)},u=n=>r.selectedSeats.some(t=>t.uniqueSeatId===n),c=n=>{if(!Array.isArray(r.bidsArray))return console.error("bidsArray is not defined or not an array."),!1;const t=r.bidsArray.find(a=>a.seat_no==n);return t?t.total_bidders>0:!1},b=n=>{if(!r.bidsArray||!Array.isArray(r.bidsArray))return console.error("bidsArray is invalid:",r.bidsArray),{highestBid:0,bidCount:0};const t=r.bidsArray.find(a=>a.seat_no==n);return t?{highestBid:t.current_bid_amount||0,bidCount:t.total_bidders||0}:{highestBid:0,bidCount:0}};return(n,t)=>(g(),v("div",null,[t[0]||(t[0]=i("br",null,null,-1)),t[1]||(t[1]=i("p",null,"\uC785\uCC30\uB0B4\uC6A9\uC774 \uC788\uB294 \uC88C\uC11D\uC740 \uC724\uACFD\uC120\uC73C\uB85C \uD45C\uC2DC\uB428.",-1)),t[2]||(t[2]=i("br",null,null,-1)),i("div",k,[(g(),v(B,null,S(40,a=>A(x,{key:a,label:`${a}\uBC88
${b(a).highestBid}\uC6D0
${b(a).bidCount}\uBA85`,class:C([{selected:u(a),bidded:c(a),selectedbidded:u(a)&&c(a)},"seat-box"]),onClick:Q=>o(a),flat:"",color:"primary","text-color":"black"},null,8,["label","class","onClick"])),64))])]))}};var L=h($,[["__scopeId","data-v-5eb91348"]]);export{I as Q,L as S,F as a};
