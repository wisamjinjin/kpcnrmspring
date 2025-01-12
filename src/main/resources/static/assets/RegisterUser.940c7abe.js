import{Q as L,m as T}from"./messageCommon.5db82364.js";import{r as i,a as w,c as E,ae as a,f as n,g as o,ay as r,e as x,d as _,w as C,b as V,Q as U,ah as $}from"./index.c44c2320.js";import{f as h,a as v,A as f}from"./sessionFunctions.3c5066a4.js";import{_ as q}from"./plugin-vue_export-helper.21dcd24c.js";const z={class:"common-container"},F={class:"input-box"},K=["disabled"],W={key:0,class:"rowflex-container"},Y={key:2},j={__name:"RegisterUser",setup(H){const d=h(["tableName","userContext"]),t=i({userId:"",password:"",password2:"",userName:"",email:"",telno:"",postCode:"",addr1:"",addr2:"",userType:""}),p=i(!1),b=i(!1),c=i(!1),y=i(!1),N=i(!1),u=i(""),g=async()=>{if(!!Q()){if(await P(),b.value){alert("\uB4F1\uB85D\uB41C \uC804\uD654\uBC88\uD638\uAC00 \uC788\uC2B5\uB2C8\uB2E4. \uB2E4\uC2DC \uC785\uB825\uD574\uC8FC\uC138\uC694."),u.value="",p.value=!1;return}t.value.authNumber="",N.value=!0;try{const l=await v.post(f.SEND_ONE_SMS,{...t.value,table:d.tableName});l.status===200&&(u.value=`\uC778\uC99D\uBC88\uD638\uAC00 \uBC1C\uC1A1\uB418\uC5C8\uC2B5\uB2C8\uB2E4. ${l.data.verificationCode}`)}catch(l){m(l)}}},k=async()=>{if(!!G()){if(await B(),y.value){alert("\uB4F1\uB85D\uB41C \uC774\uBA54\uC77C\uC774 \uC788\uC2B5\uB2C8\uB2E4. \uB2E4\uC2DC \uC785\uB825\uD574\uC8FC\uC138\uC694."),u.value="",c.value=!1;return}alert("\uC0AC\uC6A9 \uAC00\uB2A5\uD55C \uC774\uBA54\uC77C\uC785\uB2C8\uB2E4."),c.value=!0}},I=()=>{u.value="\uC804\uD654\uBC88\uD638\uAC00 \uBCC0\uACBD\uB418\uC5C8\uC2B5\uB2C8\uB2E4. \uC778\uC99D\uBC88\uD638 \uBC1C\uC1A1\uC744 \uB20C\uB7EC\uC8FC\uC138\uC694.",p.value=!1},R=()=>{u.value="\uC774\uBA54\uC77C\uC774 \uBCC0\uACBD\uB418\uC5C8\uC2B5\uB2C8\uB2E4. \uC774\uBA54\uC77C\uD655\uC778\uC744 \uB20C\uB7EC\uC8FC\uC138\uC694.",c.value=!1},S=()=>{t.value.authNumber.length===6&&D()},D=async()=>{try{(await v.post(f.VERIFY_CODE,{...t.value,table:d.tableName})).status===200&&(u.value="\uC778\uC99D\uBC88\uD638\uAC00 \uD655\uC778\uB418\uC5C8\uC2B5\uB2C8\uB2E4.",p.value=!0,N.value=!1,t.value.authNumber="")}catch(l){m(l)}},P=async()=>{try{(await v.post(f.GET_TELNO_COUNT,{telno:t.value.telno,table:d.tableName})).data.telno_count>0?b.value=!0:(u.value="\uC0AC\uC6A9\uAC00\uB2A5\uD55C \uC804\uD654\uBC88\uD638\uC785\uB2C8\uB2E4.",b.value=!1)}catch(l){b.value=!1,m(l)}},B=async()=>{try{(await v.post(f.GET_EMAIL_COUNT,{telno:t.value.telno,email:t.value.email,table:d.tableName})).data.email_count>0?y.value=!0:(u.value="\uC0AC\uC6A9\uAC00\uB2A5\uD55C \uC774\uBA54\uC77C\uC785\uB2C8\uB2E4.",y.value=!1)}catch(l){y.value=!1,m(l)}},O=async()=>{if(!c.value){alert("\uC774\uBA54\uC77C \uC720\uD6A8\uD655\uC778\uC744 \uD574\uC8FC\uC138\uC694");return}if(!!A())try{(await v.post(f.REGISTER_USER,{...t.value,table:d.tableName})).status===200&&(u.value="\uC0AC\uC6A9\uC790\uAC00 \uC131\uACF5\uC801\uC73C\uB85C \uB4F1\uB85D\uB418\uC5C8\uC2B5\uB2C8\uB2E4.",p.value=!1)}catch(l){m(l)}},A=()=>{const{password:l,password2:e,userName:s}=t.value;return l?e?l!==e?(u.value="\uBE44\uBC00\uBC88\uD638\uAC00 \uC77C\uCE58\uD558\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4.",!1):s?!0:alert("\uC0AC\uC6A9\uC790 \uC774\uB984\uC744 \uC785\uB825\uD574 \uC8FC\uC138\uC694.")&&!1:(u.value="\uD655\uC778 \uBE44\uBC00\uBC88\uD638\uB97C \uC785\uB825\uD574 \uC8FC\uC138\uC694.",!1):(u.value="\uBE44\uBC00\uBC88\uD638\uB97C \uC785\uB825\uD574 \uC8FC\uC138\uC694.",!1)},M=()=>{const{password:l,password2:e,userName:s}=t.value;if(!l)return u.value="\uBE44\uBC00\uBC88\uD638\uB97C \uC785\uB825\uD574 \uC8FC\uC138\uC694.",!1;if(!e)return u.value="\uD655\uC778 \uBE44\uBC00\uBC88\uD638\uB97C \uC785\uB825\uD574 \uC8FC\uC138\uC694.",!1;if(l!==e)return u.value="\uBE44\uBC00\uBC88\uD638\uAC00 \uC77C\uCE58\uD558\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4.",!1},Q=()=>/^\d{10,11}$/.test(t.value.telno)?!0:(alert("\uC804\uD654\uBC88\uD638\uB294 11\uC790\uB9AC \uC22B\uC790\uB85C \uC785\uB825\uD574 \uC8FC\uC138\uC694."),!1),G=()=>/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(t.value.email)?!0:(alert("\uC720\uD6A8\uD558\uC9C0 \uC54A\uC740 \uC774\uBA54\uC77C \uD615\uC2DD\uC785\uB2C8\uB2E4. \uD655\uC778\uD574 \uC8FC\uC138\uC694."),!1),m=l=>{u.value=l.response?l.response.data:l.request?T.ERR_NETWORK:T.ERR_ETC};return(l,e)=>(w(),E("div",z,[e[21]||(e[21]=a("h5",null,"\uC0C8\uB85C\uC6B4 \uC0AC\uC6A9\uC790 \uB4F1\uB85D",-1)),a("div",F,[a("label",null,[e[9]||(e[9]=n(" \uC804\uD654\uBC88\uD638: ")),o(a("input",{type:"text","onUpdate:modelValue":e[0]||(e[0]=s=>t.value.telno=s),class:"input",placeholder:"\uC804\uD654\uBC88\uD638\uB97C \uC785\uB825\uD558\uC138\uC694.",minlength:"10",maxlength:"11",autocomplete:"tel",onInput:I},null,544),[[r,t.value.telno]])]),a("button",{onClick:g,type:"button",disabled:p.value}," \uC778\uC99D\uBC88\uD638 \uBC1C\uC1A1 ",8,K),N.value?(w(),E("div",W,[o(a("input",{type:"text","onUpdate:modelValue":e[1]||(e[1]=s=>t.value.authNumber=s),class:"input",style:{"font-size":"23px","margin-right":"10px"},minlength:"6",maxlength:"6",placeholder:"\uC778\uC99D\uBC88\uD638 6\uC790\uB9AC\uB97C \uC785\uB825\uD558\uC138\uC694.",onInput:S},null,544),[[r,t.value.authNumber]]),a("button",{onClick:g,type:"button"},"\uC7AC\uBC1C\uC1A1")])):x("",!0),u.value?(w(),_(L,{key:1,class:"message-box"},{default:C(()=>[n($(u.value),1)]),_:1})):x("",!0),p.value?(w(),E("div",Y,[a("label",null,[e[10]||(e[10]=n(" \uBE44\uBC00\uBC88\uD638: ")),o(a("input",{type:"password","onUpdate:modelValue":e[2]||(e[2]=s=>t.value.password=s),class:"input",placeholder:"\uBE44\uBC00\uBC88\uD638\uB97C \uC785\uB825\uD558\uC138\uC694.",autocomplete:"new-password"},null,512),[[r,t.value.password]])]),a("label",null,[e[11]||(e[11]=n(" \uBE44\uBC00\uBC88\uD638 \uD655\uC778: ")),o(a("input",{type:"password","onUpdate:modelValue":e[3]||(e[3]=s=>t.value.password2=s),class:"input",placeholder:"\uBE44\uBC00\uBC88\uD638\uB97C \uB2E4\uC2DC \uC785\uB825\uD558\uC138\uC694.",autocomplete:"new-password",onInput:M},null,544),[[r,t.value.password2]])]),a("label",null,[e[12]||(e[12]=n(" \uC0AC\uC6A9\uC790 \uC774\uB984: ")),o(a("input",{type:"text","onUpdate:modelValue":e[4]||(e[4]=s=>t.value.userName=s),class:"input",placeholder:"\uC774\uB984\uC744 \uC785\uB825\uD558\uC138\uC694.",autocomplete:"name"},null,512),[[r,t.value.userName]])]),a("label",null,[e[13]||(e[13]=n(" \uC774\uBA54\uC77C: ")),o(a("input",{type:"email","onUpdate:modelValue":e[5]||(e[5]=s=>t.value.email=s),class:"input",placeholder:"\uC774\uBA54\uC77C\uC744 \uC785\uB825\uD558\uC138\uC694.",autocomplete:"email",onInput:R},null,544),[[r,t.value.email]])]),V(U,{onClick:k,type:"button"},{default:C(()=>e[14]||(e[14]=[n(" \uC774\uBA54\uC77C \uC720\uD6A8 \uD655\uC778 ")])),_:1}),a("label",null,[e[15]||(e[15]=n(" \uC6B0\uD3B8\uBC88\uD638: ")),o(a("input",{type:"number","onUpdate:modelValue":e[6]||(e[6]=s=>t.value.postCode=s),class:"input",placeholder:"\uC6B0\uD3B8\uBC88\uD638\uB97C \uC785\uB825\uD558\uC138\uC694.",min:"10000",max:"99999",autocomplete:"postal-code"},null,512),[[r,t.value.postCode]])]),a("label",null,[e[16]||(e[16]=n(" \uC8FC\uC18C 1: ")),o(a("input",{type:"text","onUpdate:modelValue":e[7]||(e[7]=s=>t.value.addr1=s),class:"input",placeholder:"\uC8FC\uC18C\uB97C \uC785\uB825\uD558\uC138\uC694.",autocomplete:"address-line1"},null,512),[[r,t.value.addr1]])]),a("label",null,[e[17]||(e[17]=n(" \uC8FC\uC18C 2: ")),o(a("input",{type:"text","onUpdate:modelValue":e[8]||(e[8]=s=>t.value.addr2=s),class:"input",placeholder:"\uC138\uBD80 \uC8FC\uC18C\uB97C \uC785\uB825\uD558\uC138\uC694.",autocomplete:"address-line2"},null,512),[[r,t.value.addr2]])]),e[19]||(e[19]=a("br",null,null,-1)),e[20]||(e[20]=a("br",null,null,-1)),V(U,{onClick:O,color:"primary"},{default:C(()=>e[18]||(e[18]=[n("\uC785\uB825 \uB0B4\uC6A9 \uC81C\uCD9C")])),_:1})])):x("",!0)])]))}};var te=q(j,[["__scopeId","data-v-2ed171cf"]]);export{te as default};
